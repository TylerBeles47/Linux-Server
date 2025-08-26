import java.util.*;

public class PasswordManager{
    private String appName;
    private Scanner scanner;
    private PasswordEntry currentEntry;
    private ArrayList<PasswordEntry> passwordList;


    // Constructor
    public PasswordManager(){
        this.appName = " My First Password Manager";
        this.scanner = new Scanner(System.in);
        this.passwordList = new ArrayList<>();
        System.out.println("Created a new" + appName);
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
        System.out.println("Hello " + name + " lets set up your Master Password");


        createPasswordEntry();

        
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

        this.currentEntry = new PasswordEntry(service, password);
        this.passwordList.add(currentEntry);
        System.out.println("Password entry created successfully!");

        
    }

    private void viewAllPasswords(){
        if (passwordList.isEmpty()){
            System.out.println("There are no stored passwords");
            return;
        }
        System.out.println("Here are all your passwords");

        for (int i = 0; i < passwordList.size(); i++){
            PasswordEntry entry = passwordList.get(i);
            System.out.println(entry.getDescription());
            
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

            System.out.println("\n✏️ Update Password Entry for" + service);
            System.out.println("Enter new password:");
            String newPassword = scanner.nextLine();
            currentEntry.setPassword(newPassword);
        
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