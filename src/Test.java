/*
 * Main class for test the program.
 * 
 */
public class Test {

    public static void main(String[] args) {
        JdbcQueryUtil mJdbcQueryUtil = JdbcQueryUtil.getInstance();
        mJdbcQueryUtil.findCustomer(1);
        Window window = new Window();
        window.initUI();
        window.initEvent();
    }
}
