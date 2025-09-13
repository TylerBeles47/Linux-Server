import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.sql.*;
import java.io.UnsupportedEncodingException;



public class PasswordManager{
    private String appName;
    private Scanner scanner;
    private PasswordEntry currentEntry;
    private byte[] salt;
    private SecretKey encryptionKey;
    private int userId;


    // Constructor
    public PasswordManager(){
        this.appName = " My First Password Manager";
        this.scanner = new Scanner(System.in);
        System.out.println("Created a new" + appName);
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
    private SecretKey deriveKeyFromPassword(String masterPassword) throws Exception{
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private String encrypt(String plaintext) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        
        byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    private String decrypt(String encryptedText) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[encryptedWithIv.length - 16];
        
        System.arraycopy(encryptedWithIv, 0, iv, 0, 16);
        System.arraycopy(encryptedWithIv, 16, encrypted, 0, encrypted.length);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, ivSpec);
        
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }

    public static void main(String[] args) {
        // This creates a new object (instance) of PasswordManager
        PasswordManager pm = new PasswordManager();
        
        // This calls a method on that object
        pm.start();

    }

    public void start(){
        System.out.println("Welcome to password Manager");
        System.out.println("============================");
        System.out.println("Please enter your Name");
        String name = scanner.nextLine();
        System.out.println("Hello " + name + " Please enter your Master Password: ");

        String masterPassword = scanner.nextLine();
        try {
            this.userId = authenticateOrCreateUser(name, masterPassword);
            this.encryptionKey = deriveKeyFromPassword(masterPassword);
            System.out.println("Master password setup successfully");

        } catch (Exception e){
            System.out.println("Error setting up encryption: " + e.getMessage());
            return;
        }
        
        showMenu();
    }

    private void showMenu(){
        while(true){
            System.out.println("1. Update password");
            System.out.println("2. Add a new password entry");
            System.out.println("3. View all stored passwords");
            System.out.println("4. Exit");
            String update = scanner.nextLine();
        
            switch(update){
                case "1": updatePassword(); break;
                case "2": createPasswordEntry(); break;
                case "3": viewAllPasswords(); break;
                case "4":
                    System.out.println("Goodbye");
                    return;
                default:
                    System.out.println("Invalid Option please try again");
            }
        }
    }
    

    private void createPasswordEntry(){       
        System.out.println("Please enter your service:");
        String service = scanner.nextLine();
        if (findPasswordEntry(service) != null){
            System.out.println("❌ A password for " + service + " already exists!");
            System.out.println("Use the update option to change it.");
            return;
        }
    
        System.out.println("Please enter your password");
        String password = scanner.nextLine();

        try{
            String encryptedPassword = encrypt(password);
            savePasswordToDatabase(service, encryptedPassword);
            System.out.println("✅ Password saved successfully for " + service);
        }catch (Exception e) {
            System.out.println("Error saving password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewAllPasswords(){
        try {
            List<PasswordEntry> passwords = loadPasswordsFromDatabase();
            if (passwords.isEmpty()){
                System.out.println("There are no stored passwords");
                return;
            }
            System.out.println("Here are all your passwords:");
            System.out.println("============================");

            for (PasswordEntry entry : passwords){
                try {
                    String decryptedPassword = decrypt(entry.getPassword());
                    System.out.println("Service: " + entry.getService() + ", Password: " + decryptedPassword);
                } catch (Exception e) {
                    System.out.println("Error decrypting password for " + entry.getService());
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading passwords: " + e.getMessage());
        }
    }

    
    private PasswordEntry findPasswordEntry(String service){
        try {
            List<PasswordEntry> passwords = loadPasswordsFromDatabase();
            for(PasswordEntry entry : passwords){
                if(entry.getService().equalsIgnoreCase(service))
                    return entry;
            }
        } catch (Exception e) {
            System.out.println("Error searching for password: " + e.getMessage());
        }
        return null;
    }

    private void updatePassword(){
        try {
            List<PasswordEntry> passwords = loadPasswordsFromDatabase();
            if (passwords.isEmpty()){
                System.out.println("There are no stored passwords");
                return;
            }
            
            System.out.print("Enter service name to update: ");
            String service = scanner.nextLine();

            PasswordEntry entry = findPasswordEntry(service);
            if (entry == null) {
                System.out.println("❌ No password found for: " + service);
                return;
            }

            System.out.println("\n✏️ Update Password Entry for " + service);
            System.out.println("Enter new password:");
            String newPassword = scanner.nextLine();
            try {
                String encryptedPassword = encrypt(newPassword);
                updatePasswordInDatabase(service, encryptedPassword);
                System.out.println("✅ Password updated successfully!");
            } catch (Exception e) {
                System.out.println("Error updating password: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error loading passwords: " + e.getMessage());
        }
    }
    
    private int authenticateOrCreateUser(String name, String masterPassword) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT id, salt FROM users WHERE name = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                this.salt = rs.getBytes("salt");
                return rs.getInt("id");
            } else {
                this.salt = generateSalt();
                String insertSql = "INSERT INTO users (name, salt) VALUES (?, ?) RETURNING id";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, name);
                insertStmt.setBytes(2, salt);
                ResultSet insertRs = insertStmt.executeQuery();
                insertRs.next();
                System.out.println("New user created: " + name);
                return insertRs.getInt("id");
            }
        }
    }
    
    private void savePasswordToDatabase(String service, String encryptedPassword) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO passwords (user_id, service, encrypted_password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, service);
            stmt.setString(3, encryptedPassword);
            stmt.executeUpdate();
        }
    }
    
    private List<PasswordEntry> loadPasswordsFromDatabase() throws SQLException {
        List<PasswordEntry> passwords = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT service, encrypted_password FROM passwords WHERE user_id = ? ORDER BY service";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String service = rs.getString("service");
                String encryptedPassword = rs.getString("encrypted_password");
                passwords.add(new PasswordEntry(service, encryptedPassword));
            }
        }
        return passwords;
    }
    
    private void updatePasswordInDatabase(String service, String encryptedPassword) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE passwords SET encrypted_password = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND service = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, encryptedPassword);
            stmt.setInt(2, userId);
            stmt.setString(3, service);
            stmt.executeUpdate();
        }
    }
}

    


class PasswordEntry {
    private String service;
    private String password;

    public PasswordEntry(String service, String password){
        this.service = service;
        this.password = password;
    }

    public String getService(){
        return service;
    }

    public String getPassword(){
        return password;
    }

    public String getDescription(){
        return "Service: " + service + ", Password: " + password;
    }
    

    public void setPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("Password updated for " + service);
    }
}