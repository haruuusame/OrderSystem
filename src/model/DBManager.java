import java.sql.*;
public class DBManager{
    private String URL;
    Connection con = null;
    DBManager(){
        String baseDir = System.getProperty("user.dir");
        URL = "jdbc:sqlite:" + baseDir + "/data/Menu.db";
    }
    public void connect(){
        try{
            con = DriverManager.getConnection(URL);
            System.out.println("接続成功");
        }catch(SQLException e) {
            System.err.println("[接続エラー] " + e.getMessage());
        }
        
    }
    public void disconnect(){
        if(con != null){
            try{
                con.close();
            }catch(SQLException e){
                System.err.println("[接続エラー] " + e.getMessage());
            }
        }
    }
    public MenuCatalog createMenuCatalogAll() {
        List<Menu> menus = new ArrayList<>();
        try(
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM menu;"),
            ResultSet rs = pstmt.executeQuery()
        ){
            while(rs.next()){
                int itemId = rs.getInt("itemId");
                String itemName = rs.getString("itemName");
                int price = rs.getInt("price");
                int stockQuantity = rs.getInt("stockQuantity");
                String category = rs.getString("category");
                menus.add(new Menu(itemId,itemName,price,stockQuantity,category));
            }
        }catch(SQLException e) {
            System.err.println("[エラー] " + e.getMessage());
        }
        MenuCatalog catalog = new MenuCatalog(menus);
        return catalog;
    }

    public boolean registerOrder(Order order){
        String sql = "INSERT INTO orderHistory(Id, orderId, itemId, quantity, orderTime, status) VALUES (?, ?, ?, ?, ?, ?);";
        try(
            PreparedStatement pstmt = con.prepareStatement(sql)
        ){
            pstmt.setInt(2,Order.getInt);
        }
    }
}