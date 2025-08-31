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
    private ArrayList<PasswordEntry> passwordList;
    private byte[] salt;
    private SecretKey encryptionKey;


    // Constructor
    public PasswordManager(){
        this.appName = " My First Password Manager";
        this.scanner = new Scanner(System.in);
        this.passwordList = new ArrayList<>();
        System.out.println("Created a new" + appName);
        this.salt = generateSalt();
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
            this.encryptionKey = deriveKeyFromPassword(masterPassword);
            System.out.println("Master password setup succesfully");

        } catch (Exception e){
            System.out.println("error setting up encryption:" + e.getMessage());
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
            String encryptPassword = encrypt(password);
            this.currentEntry = new PasswordEntry(service, encryptPassword);
            this.passwordList.add(currentEntry);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void viewAllPasswords(){
        if (passwordList.isEmpty()){
            System.out.println("There are no stored passwords");
            return;
        }
        System.out.println("Here are all your passwords");

        for (int i = 0; i < passwordList.size(); i++){
            PasswordEntry entry = passwordList.get(i);
            try {
                String decryptedPassword = decrypt(entry.getPassword());
                System.out.println("Service: " + entry.getService() + ", Password: " + decryptedPassword);
            } catch (Exception e) {
                System.out.println("Error decrypting password for " + entry.getService());
            }
        }
        return;

    }

    
    private PasswordEntry findPasswordEntry(String service){
        for(PasswordEntry entry : passwordList){
            if(entry.getService().equalsIgnoreCase(service))
                return entry;
            
        }
        return null;
        
    }

    private void updatePassword(){
        if (passwordList.isEmpty()){
            System.out.println("There is no valid entrys");
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
                entry.setPassword(encryptedPassword);
                System.out.println("Password updated successfully!");
            } catch (Exception e) {
                System.out.println("Error updating password: " + e.getMessage());
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