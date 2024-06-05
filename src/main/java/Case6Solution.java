import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.client.ExternalTaskClient;

public class Case6Solution {

	public static void main(String[] args) {

		/*******************
		 Dies ist das gleiche External Service Task Beispiel wie in Case 1 und soll hier
		 als Vorlage für die Umsetzung der DB Anbindung mit JDBC genutzt werden.

		 Der MySQL JDBC Driver ist bereits in der pom.xml eingetragen.
		 ********************/

		// Verbindung zur Workflow Engine aufbauen
		ExternalTaskClient client = ExternalTaskClient.create()
				.baseUrl("http://group10:d854hg23t48+f2z-fvtz8tb0b4v@192.168.111.3:8080/engine-rest").asyncResponseTimeout(1000)
				.build();

		// für das Topic "group1_sendGreeting" registrieren und die folgende Funktion
		// bei jedem Aufruf ausführen
		client.subscribe("group10_greeting").lockDuration(1000).handler((externalTask, externalTaskService) -> {

			// Variable "name" aus der Prozessinstanz auslesen
			String name = (String) externalTask.getVariable("name");
			System.out.println("Variable \"name\" from process: " + name);

			// Retrieve customer data from the database
			Customer customer = getCustomerInfo(name);
			System.out.println("Customer Info: " + customer);

			// Create a greeting message
			String completeGreeting = "Hello " + customer.getName() + ", your email is " + customer.getEmail()
					+ " and your phone number is " + customer.getPhone();

			// Create a map with results
			Map<String, Object> results = new HashMap<>();
			results.put("result", completeGreeting);

			// Task erfolgreich abschliessen und die Map "results" an die Process Engine
			// übergeben
			externalTaskService.complete(externalTask, results);
		}).open();
	}

	// Method to get customer info from the database
	private static Customer getCustomerInfo(String customerName) {
		String jdbcUrl = "jdbc:mysql://127.0.0.1/SA";
		String jdbcUser = "vl_custmgmt";
		String jdbcPassword = "d854hg23t48+f2z-fvtz8tb0b4v";
		Customer customer = null;

		String query = "SELECT custId, name, email, phone, comment, createdOn FROM customer WHERE name = ?";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
			 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, customerName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					customer = new Customer();
					customer.setCustId(resultSet.getInt("custId"));
					customer.setName(resultSet.getString("name"));
					customer.setEmail(resultSet.getString("email"));
					customer.setPhone(resultSet.getString("phone"));
					customer.setComment(resultSet.getString("comment"));
					customer.setCreatedOn(resultSet.getTimestamp("createdOn"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return customer;
	}

}

// Customer class definition
class Customer {
	private int custId;
	private String name;
	private String email;
	private String phone;
	private String comment;
	private Timestamp createdOn;

	// Getters and setters

	public int getCustId() {
		return custId;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "Customer [custId=" + custId + ", name=" + name + ", email=" + email + ", phone=" + phone + ", comment="
				+ comment + ", createdOn=" + createdOn + "]";
	}
}
