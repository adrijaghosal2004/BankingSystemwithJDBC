import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BankingSystemManagement
{
    private static final String url = "jdbc:mysql://localhost:3306/bankingsystem_db";
    private static final String username = "root";
    private static final String password = "xxxxxxxx";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        try
        {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true)
            {
                System.out.println();
                System.out.println("BANKING MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1) Create an Account ");
                System.out.println("2) Debit Money");
                System.out.println("3) Credit Money");
                System.out.println("4) Transaction ");
                System.out.println("5) Check Balance ");
                System.out.println("6) View Account Details ");
                System.out.println("7) Update Password ");
                System.out.println("8) Customer Care ");
                System.out.println("9) Exit ");
                System.out.print("Choose an Option : ");
                int choice = scanner.nextInt();
                switch (choice)
                {
                    case 1:
                        creatingAccount(connection, scanner);
                        break;
                    case 2:
                        debitMoney(connection, scanner);
                        break;
                    case 3:
                        creditMoney(connection, scanner);
                        break;
                    case 4:
                        transaction(connection, scanner);
                        break;
                    case 5:
                        checkBalance(connection, scanner);
                        break;
                    case 6:
                        accountDetails(connection, scanner);
                        break;
                    case 7:
                        updatePassword(connection, scanner);
                        break;
                    case 8:
                        customerCare();
                        break;
                    case 9:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void creatingAccount(Connection connection,Scanner scanner)
    {
        try
        {
            System.out.println("Enter name of the Account Holder : ");
            String accountHolderName = scanner.next();
            System.out.println("Enter name of the Bank : ");
            String bankName = scanner.next();
            System.out.println("Enter the Account number : ");
            int accountNumber = scanner.nextInt();
            int initialDeposit;
            do
            {
                System.out.println("Enter the Initial Deposit : ");
                initialDeposit = scanner.nextInt();
                if (initialDeposit < 2000)
                {
                    System.out.println("Minimum deposit amount is 1000. Please enter again.");
                }
            } while (initialDeposit < 2000);
            System.out.println("Enter you 6 digit Password : ");
            int password = scanner.nextInt();
            String sql = "insert into banking (name, bank_name, account_number, balance, password) " +
                    "values ('" + accountHolderName + "', '" + bankName + "', " + accountNumber + ", " + initialDeposit + ", " + password + ")";
            try(Statement statement =connection.createStatement())
            {
                int affect=statement.executeUpdate(sql);
                if(affect>0)
                {
                    System.out.println("Account created successfully");
                }
                else
                {
                    System.out.println("Failed to create account");
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void debitMoney(Connection connection, Scanner scanner)
    {
        try
        {
            System.out.println("Enter the Account Number : ");
            int account =scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if(!accountExist(connection, account))
            {
                System.out.println("Account does not exist");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Password : ");
            int password = scanner.nextInt();
            System.out.println("Checking for valid password...........");
            if(!checkPassword(connection,account,password))
            {
                System.out.println("Password does not match. Please try again.");
                return;
            }
            System.out.println("The password is valid");
            System.out.print("Enter the amount to be Debited : ");
            int money =scanner.nextInt();
            int balance = getBalance(connection, account);
            if (balance < money) {
                System.out.println("Insufficient balance");
                return;
            }
            if(balance-money<2000)
            {
                System.out.println("The Minimum Account Balance must be maintained at 2000");
                return;
            }
            String sql = "update banking set balance = balance - "+ money +" where account_number = "+account;
            try(Statement statement =connection.createStatement())
            {
                int affect= statement.executeUpdate(sql);
                if(affect>0)
                {
                    System.out.println("Debited " + money + " from account number " + account );
                }
                else
                {
                    System.out.println("Account not found");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void creditMoney(Connection connection, Scanner scanner)
    {
        try{
            System.out.println("Enter the Account Number : ");
            int account =scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if(!accountExist(connection, account))
            {
                System.out.println("Account does not exist");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Password : ");
            int password = scanner.nextInt();
            System.out.println("Checking for valid password...........");
            if(!checkPassword(connection,account,password))
            {
                System.out.println("Password does not match. Please try again.");
                return;
            }
            System.out.println("The password is valid");
            System.out.print("Enter the amount to be Credited : ");
            int money =scanner.nextInt();
            String sql = "update banking set balance = balance + "+ money +" where account_number = "+account;
            try(Statement statement =connection.createStatement())
            {
                int affect= statement.executeUpdate(sql);
                if(affect>0)
                {
                    System.out.println("Credited " + money + " to account number " + account );
                }
                else
                {
                    System.out.println("Account not found");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void transaction(Connection connection, Scanner scanner)
    {
        try
        {
            System.out.println("Enter the Sender's Account Number : ");
            int sAccount = scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if(!accountExist(connection, sAccount))
            {
                System.out.println("Account does not exist");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Recipient's Account Number : ");
            int rAccount = scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if(!accountExist(connection, rAccount))
            {
                System.out.println("Account does not exist. So can not send money to this Account");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Sender's Password to Authenticate : ");
            int password = scanner.nextInt();
            System.out.println("Checking for valid password...........");
            if(!checkPassword(connection, sAccount,password))
            {
                System.out.println("Password does not match. Please try again.");
                return;
            }
            System.out.println("The password is valid");
            System.out.print("Enter the amount to be Transferred : ");
            int money =scanner.nextInt();
            int balance = getBalance(connection, sAccount);
            if (balance < money) {
                System.out.println("Insufficient balance");
                return;
            }
            if(balance-money<2000)
            {
                System.out.println("The Minimum Account Balance must be maintained at 2000");
                return;
            }
            if(money>10000)
            {
                System.out.println("Transfers exceeding 10,000 in a day are not permitted");
                return;
            }
            String sql = "update banking set balance = balance - "+ money +" where account_number = "+ sAccount;
            String sql1 = "update banking set balance = balance + "+ money +" where account_number = "+rAccount;
            try(Statement statement =connection.createStatement())
            {
                int affect= statement.executeUpdate(sql);
                if(affect>0)
                {
                    int affect1= statement.executeUpdate(sql1);
                    if(affect1>0)
                    {
                        System.out.println("Transaction successful");
                    }
                }
                else
                {
                    System.out.println("Transaction failed");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void checkBalance(Connection connection, Scanner scanner)
    {
        try {
            System.out.println("Enter the Account Number : ");
            int account = scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if (!accountExist(connection, account))
            {
                System.out.println("Account does not exist");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Password : ");
            int password = scanner.nextInt();
            System.out.println("Checking for valid password...........");
            if(!checkPassword(connection,account,password))
            {
                System.out.println("Password does not match. Please try again.");
                return;
            }
            System.out.println("The password is valid");
            String sql = "select balance from banking where account_number = " + account;
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql))
            {
                System.out.println("Account Balance");
                while (rs.next())
                {
                    int balance = rs.getInt("balance");
                    System.out.println(balance);
                }
            }
            }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void accountDetails(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter the Account Number : ");
            int account = scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if (!accountExist(connection, account))
            {
                System.out.println("Account does not exist");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Password : ");
            int password = scanner.nextInt();
            System.out.println("Checking for valid password...........");
            if(!checkPassword(connection,account,password))
            {
                System.out.println("Password does not match. Please try again.");
                return;
            }
            System.out.println("The password is valid");
            String sql = "select * from banking where account_number = " + account;
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(sql))
            {
                System.out.println("Account Balance");
                while (rs.next())
                {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String bankName = rs.getString("bank_name");
                    int accnum = rs.getInt("account_number");
                    int balance = rs.getInt("balance");

                    System.out.println("Id : " + id);
                    System.out.println("Account Holder Name : " + name);
                    System.out.println("Bank Name : " + bankName);
                    System.out.println("Account Number : " + accnum);
                    System.out.println("Account Balance : " + balance);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void updatePassword(Connection connection, Scanner scanner){
        try {
            System.out.println("Enter the Account Number : ");
            int account = scanner.nextInt();
            System.out.println("Checking for valid account number...........");
            if (!accountExist(connection, account))
            {
                System.out.println("Account does not exist");
                return;
            }
            System.out.println("The account number is valid");
            System.out.println("Enter the Old Password : ");
            int oldPassword = scanner.nextInt();
            System.out.println("Checking for valid password...........");
            if(!checkPassword(connection,account,oldPassword))
            {
                System.out.println("Password does not match. Please try again.");
                return;
            }
            System.out.println("The password is valid");
            System.out.println("Enter the New Password : ");
            int newPassword = scanner.nextInt();
            String sql="update banking set password="+ newPassword +" where account_number = "+account;
            try(Statement statement =connection.createStatement())
            {
                int affect= statement.executeUpdate(sql);
                if(affect>0)
                {
                    System.out.println("Password updated successfully");
                }
                else {
                    System.out.println("Password updation failed");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static boolean accountExist(Connection connection, int account)
    {
        try
        {
            String sql="select * from banking where account_number=" + account;
            try(Statement statement =connection.createStatement();
                ResultSet rs=statement.executeQuery(sql))
            {
                return rs.next();

            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static boolean checkPassword(Connection connection, int account, int password)
    {
        try
        {
            String sql = "select * from banking where account_number = " + account + " and password = " + password;
            try(Statement statement =connection.createStatement();
                ResultSet rs=statement.executeQuery(sql))
            {
                return rs.next();

            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static int getBalance(Connection connection, int account)
    {
        try
        {
            String sql="select balance from banking where account_number=" + account;
            try(Statement statement =connection.createStatement();
                ResultSet rs=statement.executeQuery(sql))
            {
                if (rs.next())
                {
                    int balance = rs.getInt("balance");
                    return balance;
                }
                else
                {
                    System.out.println("Account not found.");
                    return -1;
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private static void customerCare()
    {
        System.out.println("Welcome to Customer Care!");
        System.out.println("For any queries, Call our helpline number: 987568xxxx ");
        System.out.println("Please note that the helpline number is available from 10 AM to 5 PM.");
        System.out.println("Thank you for contacting us!");
    }

    private static void exit()
    {
        System.out.println("Exiting the Banking System");
        System.out.println("Thank you for using our services!");
    }
}
