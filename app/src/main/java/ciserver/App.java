/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

/**
 * This class is the initial class used to start the whole CI server program
 */
public class App {
    /**
     * Gets a greeting, in this case "Hello world!"
     *
     * @return String of the greeting to print
     */
    public String getGreeting() {
        return "Hello World!";
    }

    /**
     * Print a greeting then launch the CI server
     *
     * @param args the arguments to the program, not used
     */
    public static void main(String[] args) {
        System.out.println(new App().getGreeting());

        try {
            ContinuousIntegrationServer.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
