import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import model.Customer;
import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Case6Solution {

	private static final String JDBC_URL = "jdbc:mysql://192.168.111.13/";
	private static final String JDBC_USER = "vl_custmgmt";
	private static final String JDBC_PASSWD = "d854hg23t48+f2z-fvtz8tb0b4v";

	private static final String WORKFLOW_ENGINE_URL = "192.168.111.3:8080/engine-rest";
	private static final String WORKFLOW_ENGINE_USER = "group10";
	private static final String WORKFLOW_ENGINE_PASSWD = "Pliuzbi7vt8Ioud";

	private static final Logger logger = LoggerFactory.getLogger(Case6Solution.class);

	public static void main(String[] args) {
		try {
			logger.info("Starting the workflow engine connection setup.");

			// Verbindung zur Workflow Engine aufbauen
			ExternalTaskClient client = ExternalTaskClient.create()
					.baseUrl("http://"+WORKFLOW_ENGINE_USER+":"+WORKFLOW_ENGINE_PASSWD+"@" + WORKFLOW_ENGINE_URL).asyncResponseTimeout(1000)
					.build();

			logger.info("Connected to the workflow engine.");

			// für das Topic "group10_greeting" registrieren und die folgende Funktion bei jedem Aufruf ausführen
			client.subscribe("group10_greeting").lockDuration(1000).handler((externalTask, externalTaskService) -> {
				// Variable "name" aus der Prozessinstanz auslesen
				String name = externalTask.getVariable("name");
				logger.info("Received variable 'name' from process: {}", name);

				// Retrieve customer data from the database
				Customer customer = getCustomerInfo(name);
				if (customer != null) {
					logger.info("Customer Info: {}", customer);

					// Create a greeting message
					String completeGreeting = "Hello " + customer.getName() + ", your email is " + customer.getEmail()
							+ " and your phone number is " + customer.getPhone();

					// Create a map with results
					Map<String, Object> results = new HashMap<>();
					results.put("result", completeGreeting);

					// Task erfolgreich abschliessen und die Map "results" an die Process Engine übergeben
					externalTaskService.complete(externalTask, results);
					logger.info("Task completed successfully with results: {}", results);
				} else {
					logger.error("Customer not found for name: {}", name);
				}
			}).open();

			// New external service task registration for the additional task
			client.subscribe("group10_newTask").lockDuration(1000).handler((externalTask, externalTaskService) -> {
				// Variable "customerId" aus der Prozessinstanz auslesen
				Integer customerId = externalTask.getVariable("customerId");
				logger.info("Received variable 'customerId' from process: {}", customerId);

				// Perform some processing for the new task
				String customerDetails = processCustomerDetails(customerId);
				logger.info("Processed Customer Details: {}", customerDetails);

				// Create a map with results
				Map<String, Object> results = new HashMap<>();
				results.put("customerDetails", customerDetails);

				// Task erfolgreich abschliessen und die Map "results" an die Process Engine übergeben
				externalTaskService.complete(externalTask, results);
				logger.info("Task completed successfully with results: {}", results);
			}).open();
		} catch (Exception e) {
			logger.error("An error occurred while setting up the workflow engine connection.", e);
		}
	}

	// Method to get customer info from the database
	private static Customer getCustomerInfo(String customerName) {
		final Logger logger = LoggerFactory.getLogger(Case6Solution.class);

		Customer customer = null;
		final String query = "SELECT custId, name, email, phone, comment, createdOn FROM customer WHERE name = ?";

		try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWD);
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
			logger.error("An error occurred while retrieving customer info for name: {}", customerName, e);
		}

		return customer;
	}

	// New method to process customer details for the new task
	private static String processCustomerDetails(Integer customerId) {
		final Logger logger = LoggerFactory.getLogger(Case6Solution.class);
		// Placeholder for the actual processing logic
		String processedDetails = "Processed details for customer with ID: " + customerId;
		logger.debug("Processing details for customerId: {}", customerId);
		return processedDetails;
	}
}