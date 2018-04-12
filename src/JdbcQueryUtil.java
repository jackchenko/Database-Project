import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class JdbcQueryUtil {

    String jdbcClassName="com.ibm.db2.jcc.DB2Driver";
    String url="jdbc:db2:c3421a";

    Connection connection = null;
    String  queryText = "";     // The SQL text.
    PreparedStatement querySt   = null;   // The query handle.
    ResultSet answers   = null;   // A cursor.


    public static JdbcQueryUtil instance = new JdbcQueryUtil();

    private JdbcQueryUtil() {
        try {
            try {
                Class.forName(jdbcClassName).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            System.out.println("Start Connecting");
            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            if(connection!=null){
                System.out.println("Connected successfully.");
            }
        }
    }

    public static JdbcQueryUtil getInstance() {
        return instance;
    }
/*
 * Find customer with given id. 
 */
    public Customer findCustomer(int cid) {
        Customer customer = null;
        queryText = "SELECT * " + "FROM yrb_customer " + "WHERE cid = ?";
        try {
            querySt = connection.prepareStatement(queryText);
            querySt.setInt(1, cid);
            answers = querySt.executeQuery();
            if (answers.next()) {
                customer = new Customer();
                customer.setCid(cid);
                customer.setName(answers.getString("name"));
                customer.setCity(answers.getString("city"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }
/*
 * Find member that belongs to which clubs.
 */
    public Member findMember(int cid) {
        Member member = null;
        queryText = "SELECT * " + "FROM yrb_member " + "WHERE cid = ?";
        try {
            querySt = connection.prepareStatement(queryText);
            querySt.setInt(1, cid);
            answers = querySt.executeQuery();
            if (answers.next()) {
                member = new Member();
                member.setCid(cid);
                member.setClub(answers.getString("club"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }
/*
 * Fetch all categories from database.
 */
    public List<Category> fetchCategories() {
        List<Category> categories = new ArrayList<>();
        queryText = "SELECT * " + "FROM yrb_category";
        try {
            querySt = connection.prepareStatement(queryText);
            answers = querySt.executeQuery();
            while (answers.next()) {
                Category category = new Category();
                category.setCat(answers.getString("cat"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
/*
 *Find book from yrb_book with given partial title in given category. 
 */
    public List<Book> findBook(String title, String cat) {
        List<Book> books = new ArrayList<>();
        queryText = "SELECT * " + "FROM yrb_book " + "WHERE title LIKE ? AND cat = ?";
        try {
            querySt = connection.prepareStatement(queryText);
            querySt.setString(1, "%" + title + "%");
            querySt.setString(2, cat);
            answers = querySt.executeQuery();
            while (answers.next()) {
                Book book = new Book();
                book.setTitle(answers.getString("title"));
                book.setCat(cat);
                book.setLanguage(answers.getString("language"));
                book.setYear(answers.getInt("year"));
                book.setWeight(answers.getInt("weight"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
/*
 * Shows the minimum price that can be offered by customer's club. Customers are not allowed to buy books in the club without member.
 */
    public Offer minPrice(int cid, String title) {

    	Offer offer = null;
        queryText = "SELECT * FROM yrb_offer WHERE club in (SELECT club FROM yrb_member where cid = ?) and title = ? ORDER BY price limit 1";
        try {
            querySt = connection.prepareStatement(queryText);
            querySt.setInt(1, cid);
            querySt.setString(2, title);
            answers = querySt.executeQuery();
            if (answers.next()) {
                offer = new Offer();
                offer.setTitle(title);
                offer.setYear(answers.getInt("year"));
                offer.setClub(answers.getString("club"));
                offer.setPrice(answers.getFloat("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offer;
    }
/*
 * Insert the purchase into yrb_purchase.
 */
    public boolean insertPurchase(Purchase purchase) {
        boolean result = false;

        queryText = "insert into yrb_purchase (cid,club,title,year,when,qnty) values (?, ?, ?, ?, ?, ?)";
        try {
            querySt = connection.prepareStatement(queryText);
            querySt.setInt(1, purchase.getCid());
            querySt.setString(2, purchase.getClub());
            querySt.setString(3, purchase.getTitle());
            querySt.setInt(4, purchase.getYear());
            querySt.setTimestamp(5, new Timestamp(purchase.getWhen().getTime()));
            querySt.setInt(6, purchase.getQnty());
            result = querySt.execute();
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
