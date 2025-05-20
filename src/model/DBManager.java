package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager{
    private String URL;
    private Connection con = null;
    public DBManager(String filename){
        String baseDir = System.getProperty("user.dir");
        URL = "jdbc:sqlite:" + baseDir + "/data/" + filename;
    }

    // DBに接続
    public void connect(){
        try{
            con = DriverManager.getConnection(URL);
            con.setAutoCommit(true);
            System.out.println("接続成功");
        }catch(SQLException e) {
            System.err.println("[接続エラー] " + e.getMessage());
        }
        
    }
    // DB接続解除
    public void disconnect(){
        if(con != null){
            try{
                con.close();
            }catch(SQLException e){
                System.err.println("[接続エラー] " + e.getMessage());
            }
        }
    }

    //全カタログを取得
    public MenuCatalog createMenuCatalogAll() {
        List<Menu> menus = new ArrayList<>();
        try(
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM menu");
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

    // 注文をDBに登録
    public boolean registerOrder(Order order){
        boolean isOk = true;
        String insertSql = "INSERT INTO orderHistory(orderId, itemId, quantity, orderTime, status) VALUES (?, ?, ?, ?, ?);";
        String updateSql = "UPDATE menu SET stockQuantity = stockQuantity - ? WHERE itemId = ? AND stockQuantity >= ?";
        try(
            PreparedStatement insertStmt = con.prepareStatement(insertSql);
            PreparedStatement updateStmt = con.prepareStatement(updateSql)
        ){
            con.setAutoCommit(false);
            for(OrderLine line:order.asList()) {
                // 在庫が十分な場合に減算
                updateStmt.setInt(1, line.getQuantity());
                updateStmt.setInt(2,line.getMenu().getItemId());
                updateStmt.setInt(3, line.getQuantity());
                int updated = updateStmt.executeUpdate();

                // 更新がなされていなければ失敗=在庫不足
                if (updated == 0) {
                    throw new SQLException("在庫不足:itemId=" + line.getMenu().getItemId());
                }

                // テーブルへ追加
                insertStmt.setInt(1,order.getOrderId());
                insertStmt.setInt(2,line.getMenu().getItemId());
                insertStmt.setInt(3,line.getQuantity());
                insertStmt.setString(4,order.getOrderDate().toString());
                insertStmt.setInt(5,0);
                insertStmt.executeUpdate();
            }
            con.commit();
        }catch(SQLException e){
            isOk = false;
            try{
                con.rollback();
                System.err.println("[ロールバック]" + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.err.println("[ロールバック失敗]" + rollbackEx.getMessage());
            }
        }finally{
            try{
                con.setAutoCommit(true);
            }catch(SQLException e) {
                System.out.println("[エラー]"+e.getMessage());
            }
        }
        return isOk;
    }

    // 在庫補充をDBに反映
    public boolean restockMenuItem(int itemId, int quantity){
        boolean isOk = true;
        String sql = "UPDATE menu set stockQuantity = stockQuantity + ? WHERE itemId = ?";
        try(
            PreparedStatement pstmt = con.prepareStatement(sql)
        ){
            pstmt.setInt(1,quantity);
            pstmt.setInt(2,itemId);
            int updated = pstmt.executeUpdate();

            if (updated == 0) {
                throw new SQLException("商品が見つかりません:itemId=" + itemId);
            }

        }catch(SQLException e){
            isOk = false;
            System.err.println("[エラー]"+e.getMessage());
        }
        return isOk;
    }

    // メニューを新規作成
    public Menu addNewMenuItem(String itemName,int price,int stockQuantity,String category){
        String sql = "INSERT INTO menu(itemName, price, stockQuantity, category) VALUES (?, ?, ?, ?);";
        try(
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ){
            pstmt.setString(1,itemName);
            pstmt.setInt(2,price);
            pstmt.setInt(3,stockQuantity);
            pstmt.setString(4,category);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if(keys.next()) {
                int itemId = keys.getInt(1);
                return new Menu(itemId,itemName,price,stockQuantity,category);
            }else{
                throw new SQLException("itemIdの生成に失敗しました");
            }
        }catch(SQLException e){
            System.err.println("[エラー]:"+e.getMessage());
            return null;
        }
    }

    // ステータスを更新
    public void updateStatus(int orderId,int itemId,int status){
        String sql = "UPDATE orderHistory SET status = ? WHERE orderId = ? AND itemId = ?";
        try(
            PreparedStatement pstmt = con.prepareStatement(sql);
        ){
            pstmt.setInt(1,status);
            pstmt.setInt(2,orderId);
            pstmt.setInt(3,itemId);
            pstmt.executeUpdate();
        }catch(SQLException e){
            System.err.println("[エラー]:"+e.getMessage());
        }
    }
}