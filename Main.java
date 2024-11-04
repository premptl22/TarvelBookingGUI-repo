import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Trip> trips = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static User currentUser = null; // Added to keep track of the current user

    public static void main(String[] args) {
        loadUsers(); // Load users from file
        initializeTrips();
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void initializeTrips() {
        trips.add(new Trip("Paris", "30-10-2024", "10-11-2024", 1500.0, 2000.0, 2200.0, "paris.jpg"));
        trips.add(new Trip("Tokyo", "01-11-2024", "11-11-2024", 2000.0, 2500.0, 2700.0, "tokyo.jpg"));
        trips.add(new Trip("New York", "15-11-2024", "30-11-2024", 1800.0, 2300.0, 2500.0, "newyork.jpg"));
        trips.add(new Trip("Sydney", "20-11-2024", "10-12-2024", 2200.0, 2700.0, 3000.0, "sydney.jpg"));
    }

    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length == 4) { // Check if the line has all the required details
                    users.add(new User(userDetails[0], userDetails[1], userDetails[2], userDetails[3]));
                } else {
                    System.out.println("Invalid user details: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getEmail() + ","
                        + user.getMobileNumber() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Travel Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10)); // Adjusted GridLayout for better layout
        JButton registerButton = createImageButton("Register", "register.png"); // Register button should be first
        JButton loginButton = createImageButton("Login", "login.png"); // Added login button
        JButton viewTripsButton = createImageButton("View Trips", "view_trips.png");
        JButton viewAirlineDetailsButton = createImageButton("View Airline Details", "view_airline_details.png");
        JButton bookTripButton = createImageButton("Book Trip", "book_trip.png");
        JButton viewBookingsButton = createImageButton("View Bookings", "view_bookings.png");
        JButton feedbackFormButton = createImageButton("Feedback Form", "feedback_form.png"); // Added feedback form
                                                                                              // button
        JButton tripWishButton = createImageButton("Wishing!", "trip_wish.png"); // Changed the name of the button to
                                                                                 // "Wishing!"
        JButton companyDetailsButton = createImageButton("Company Details", "company_details.png"); // Added company
                                                                                                    // details button

        registerButton.addActionListener(e -> {
            register();
            saveUsers(); // Save users to file after registration
            currentUser = null; // Reset current user after registration
        }); // Added register action
        loginButton.addActionListener(e -> {
            currentUser = login();
            if (currentUser != null) {
                JOptionPane.showMessageDialog(null, "Login successful!");
            }
        }); // Added login action
        viewTripsButton.addActionListener(e -> viewTrips());
        viewAirlineDetailsButton.addActionListener(e -> viewAirlineDetails());
        bookTripButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(null, "Please login or register before booking a trip.");
            } else {
                bookTrip();
            }
        });
        viewBookingsButton.addActionListener(e -> viewBookings());
        feedbackFormButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(null, "Please login or register before filling the feedback form.");
            } else if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(null, "You have not booked any trip yet, so you cannot give feedback.");
            } else {
                fillFeedbackForm();
            }
        }); // Added feedback form action
        tripWishButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "We hope that your trip is going great and you enjoy a lot there!");
        }); // Added trip wish action
        companyDetailsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "<html>Company Name: Travel Booking<br>Contact Number: 8320504292<br>Email Address: travelbookingsystem@gmail.com<br>Operating Hours: 24 Hours<br>About Us: We help the customer to book their trips easily!</html>");
        }); // Added company details action

        panel.add(registerButton); // Register button should be first
        panel.add(loginButton); // Added login button to the panel
        panel.add(viewTripsButton);
        panel.add(viewAirlineDetailsButton);
        panel.add(bookTripButton);
        panel.add(viewBookingsButton);
        panel.add(feedbackFormButton); // Added feedback form button to the panel
        panel.add(tripWishButton); // Added trip wish button to the panel
        panel.add(companyDetailsButton); // Added company details button to the panel

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JButton createImageButton(String text, String imagePath) {
        JButton button = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        } catch (Exception e) {
            System.out.println("Error loading image: " + imagePath);
        }
        return button;
    }

    private static void viewTrips() {
        JFrame tripsFrame = new JFrame("Available Trips");
        tripsFrame.setLayout(new GridLayout(0, 1));
        tripsFrame.setSize(600, 400);

        for (Trip trip : trips) {
            JPanel tripPanel = new JPanel();
            tripPanel.setLayout(new BorderLayout());

            JLabel imageLabel = new JLabel(new ImageIcon(trip.getImagePath()));
            tripPanel.add(imageLabel, BorderLayout.WEST);

            JLabel infoLabel = new JLabel(trip.toString());
            tripPanel.add(infoLabel, BorderLayout.CENTER);

            tripsFrame.add(tripPanel);
        }

        tripsFrame.setVisible(true);
    }

    private static void bookTrip() {
        if (trips.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No trips available to book.");
            return;
        }
        String[] tripOptions = new String[trips.size()];
        for (int i = 0; i < trips.size(); i++) {
            tripOptions[i] = (i + 1) + ". " + trips.get(i).getDestination();
        }
        String selectedTrip = (String) JOptionPane.showInputDialog(null, "Select a trip to book:",
                "Book Trip", JOptionPane.QUESTION_MESSAGE, null, tripOptions, tripOptions[0]);
        if (selectedTrip != null) {
            int selectedIndex = Integer.parseInt(selectedTrip.split("\\.")[0]) - 1;
            String customerName = JOptionPane.showInputDialog("Enter customer name:");
            if (customerName == null || customerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Customer name is required for booking.");
                return;
            }
            String[] packageOptions = { "Economy", "Premium", "Business" };
            String selectedPackage = (String) JOptionPane.showInputDialog(null, "Select a package:",
                    "Book Trip", JOptionPane.QUESTION_MESSAGE, null, packageOptions, packageOptions[0]);
            if (selectedPackage != null) {
                bookings.add(new Booking(customerName, trips.get(selectedIndex), selectedPackage));
                JOptionPane.showMessageDialog(null, "Trip booked successfully!");
            }
        }
    }

    private static void viewBookings() {
        JFrame bookingsFrame = new JFrame("All Bookings");
        bookingsFrame.setLayout(new GridLayout(0, 1));
        bookingsFrame.setSize(600, 400);

        if (bookings.isEmpty()) {
            JLabel noBookingsLabel = new JLabel("No bookings yet", SwingConstants.CENTER);
            noBookingsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            bookingsFrame.add(noBookingsLabel);
        } else {
            for (Booking booking : bookings) {
                JPanel bookingPanel = new JPanel();
                bookingPanel.setLayout(new BorderLayout());

                JLabel imageLabel = new JLabel(new ImageIcon(booking.getTrip().getImagePath()));
                bookingPanel.add(imageLabel, BorderLayout.WEST);

                JLabel infoLabel = new JLabel(booking.toString());
                bookingPanel.add(infoLabel, BorderLayout.CENTER);

                bookingsFrame.add(bookingPanel);
            }
        }

        bookingsFrame.setVisible(true);
    }

    private static void viewAirlineDetails() {
        JFrame airlineDetailsFrame = new JFrame("Airline Details");
        airlineDetailsFrame.setLayout(new GridLayout(0, 1));
        airlineDetailsFrame.setSize(600, 400);

        if (bookings.isEmpty()) {
            for (Trip trip : trips) {
                JPanel airlineDetailsPanel = new JPanel();
                airlineDetailsPanel.setLayout(new BorderLayout());

                JLabel airlineDetailsLabel = new JLabel("<html>Airline: " + trip.getDestination()
                        + "<br>Flight Number: " + trip.getDestination() + "123</html>");
                airlineDetailsPanel.add(airlineDetailsLabel, BorderLayout.CENTER);

                // Adding available booking options or packages for each trip
                JLabel packagesLabel = new JLabel("<html>Available Packages:<br>- Economy: $" + trip.getPrice("Economy")
                        + "<br>- Premium: $" + trip.getPrice("Premium") + "<br>- Business: $"
                        + trip.getPrice("Business") + "</html>");
                airlineDetailsPanel.add(packagesLabel, BorderLayout.EAST);

                airlineDetailsFrame.add(airlineDetailsPanel);
            }
        } else {
            for (Trip trip : trips) {
                JPanel airlineDetailsPanel = new JPanel();
                airlineDetailsPanel.setLayout(new BorderLayout());

                JLabel airlineDetailsLabel = new JLabel("<html>Airline: " + trip.getDestination()
                        + "<br>Flight Number: " + trip.getDestination() + "123</html>");
                airlineDetailsPanel.add(airlineDetailsLabel, BorderLayout.CENTER);

                // Adding available booking options or packages for each trip
                JLabel packagesLabel = new JLabel("<html>Available Packages:<br>- Economy: $" + trip.getPrice("Economy")
                        + "<br>- Premium: $" + trip.getPrice("Premium") + "<br>- Business: $"
                        + trip.getPrice("Business") + "</html>");
                airlineDetailsPanel.add(packagesLabel, BorderLayout.EAST);

                airlineDetailsFrame.add(airlineDetailsPanel);
            }
        }

        airlineDetailsFrame.setVisible(true);
    }

    private static void fillFeedbackForm() {
        String customerName = JOptionPane.showInputDialog("Enter customer name:");
        if (customerName == null || customerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Customer name is required for feedback.");
            return;
        }
        String[] userExperienceOptions = { "Good", "Very Good", "Excellent" };
        String userExperience = (String) JOptionPane.showInputDialog(null, "Select user experience:",
                "Feedback Form", JOptionPane.QUESTION_MESSAGE, null, userExperienceOptions, userExperienceOptions[0]);
        if (userExperience != null) {
            String userSuggestions = JOptionPane.showInputDialog("Enter user suggestions:");
            String[] reportIssueOptions = { "Booking Issues", "Technical Issues", "Payment Issues", "Account Issues",
                    "Service Issues", "Miscellaneous Issues", "None" };
            String reportIssue = (String) JOptionPane.showInputDialog(null, "Select report issue(s):",
                    "Feedback Form", JOptionPane.QUESTION_MESSAGE, null, reportIssueOptions, reportIssueOptions[0]);
            if (reportIssue != null) {
                JOptionPane.showMessageDialog(null, "Feedback submitted successfully!");
            }
        }
    }

    private static User login() {
        if (users.size() > 1) {
            String[] userOptions = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                userOptions[i] = (i + 1) + ". " + users.get(i).getUsername();
            }
            String selectedUser = (String) JOptionPane.showInputDialog(null, "Select a user to login:",
                    "Login", JOptionPane.QUESTION_MESSAGE, null, userOptions, userOptions[0]);
            if (selectedUser != null) {
                int selectedIndex = Integer.parseInt(selectedUser.split("\\.")[0]) - 1;
                String password = JOptionPane.showInputDialog("Enter password:");
                if (password == null || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Password is required for login.");
                    return null;
                }
                if (users.get(selectedIndex).getPassword().equals(password)) {
                    return users.get(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password.");
                    return null;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a user to login.");
                return null;
            }
        } else {
            String username = JOptionPane.showInputDialog("Enter username:");
            String password = JOptionPane.showInputDialog("Enter password:");
            User validUser = null;
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    validUser = user;
                    break;
                }
            }
            if (validUser != null) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                return validUser;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
                return null;
            }
        }
    }

    private static void register() {
        String username = JOptionPane.showInputDialog("Enter new username:");
        String password = JOptionPane.showInputDialog("Enter new password:");
        String email = JOptionPane.showInputDialog("Enter email:");
        String mobileNumber = JOptionPane.showInputDialog("Enter mobile number:");
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()
                || email == null || email.trim().isEmpty() || mobileNumber == null || mobileNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required for registration.");
            return;
        }
        users.add(new User(username, password, email, mobileNumber));
        JOptionPane.showMessageDialog(null, "Registration successful!");
    }
}

class Trip {
    private String destination;
    private String startDate;
    private String endDate;
    private double economyPrice;
    private double premiumPrice;
    private double businessPrice;
    private String imagePath;

    public Trip(String destination, String startDate, String endDate, double economyPrice, double premiumPrice,
            double businessPrice, String imagePath) {
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.economyPrice = economyPrice;
        this.premiumPrice = premiumPrice;
        this.businessPrice = businessPrice;
        this.imagePath = imagePath;
    }

    public String getDestination() {
        return destination;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return "<html>Trip to " + destination + "<br>From " + startDate + " to " + endDate + "<br>Economy: $"
                + economyPrice + "<br>Premium: $" + premiumPrice + "<br>Business: $" + businessPrice
                + "</html>";
    }

    public double getPrice(String packageType) {
        switch (packageType) {
            case "Economy":
                return economyPrice;
            case "Premium":
                return premiumPrice;
            case "Business":
                return businessPrice;
            default:
                return 0.0;
        }
    }
}

class Booking {
    private String customerName;
    private Trip trip;
    private String packageType;

    public Booking(String customerName, Trip trip, String packageType) {
        this.customerName = customerName;
        this.trip = trip;
        this.packageType = packageType;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getPackageType() {
        return packageType;
    }

    @Override
    public String toString() {
        return "<html>" + customerName + " booked:<br>" + trip.toString() + "<br>Package: " + packageType + "</html>";
    }
}

class User {
    private String username;
    private String password;
    private String email;
    private String mobileNumber;

    public User(String username, String password, String email, String mobileNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}