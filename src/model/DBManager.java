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
            return new MenuCatalog(menus);
        }catch(SQLException e) {
            System.err.println("[エラー] " + e.getMessage());
        }
        return null;
    }

    // 注文をDBに登録
    public Order registerOrder(Order preOrder){
        String insertHeaderSql = "INSERT INTO order_header(orderDate,status) VALUES (?, ?)";
        String insertDetailSql = "INSERT INTO order_detail(orderId,itemId,quantity,status) VALUES (?, ?, ?, ?)";
        String updateMenuSql = "UPDATE menu SET stockQuantity = stockQuantity - ? WHERE itemId = ? AND stockQuantity >= ?";
        try(
            PreparedStatement insertHeaderStmt = con.prepareStatement(insertHeaderSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertDetailStmt = con.prepareStatement(insertDetailSql);
            PreparedStatement updateMenuStmt = con.prepareStatement(updateMenuSql)
        ){
            con.setAutoCommit(false); // rollback可能状態へ変更
            // 注文の概要をheaderテーブルへ追加
            insertHeaderStmt.setString(1, preOrder.getOrderDate().toString());
            insertHeaderStmt.setInt(2,0);
            int registered_header = insertHeaderStmt.executeUpdate();

            // 更新がなされていなければ失敗
            if(registered_header == 0) {
                throw new SQLException("注文の登録に失敗しました");
            }

            ResultSet keys = insertHeaderStmt.getGeneratedKeys();
            int orderId;
            if(keys.next()) {
                orderId = keys.getInt(1);
            }else{
                    throw new SQLException("OrderIdの生成に失敗しました");
            }
            // 注文の詳細を反映

            for(OrderLine line:preOrder.asList()) {
                // 在庫が十分な場合にmenuテーブルの在庫数を減算
                updateMenuStmt.setInt(1, line.getQuantity());
                updateMenuStmt.setInt(2,line.getMenu().getItemId());
                updateMenuStmt.setInt(3, line.getQuantity());
                int updated_menu = updateMenuStmt.executeUpdate();

                // 更新がなされていなければ失敗=在庫不足
                if (updated_menu == 0) {
                    throw new SQLException("在庫不足:itemId=" + line.getMenu().getItemId());
                }

                // detailテーブルへ追加
                insertDetailStmt.setInt(1, orderId);
                insertDetailStmt.setInt(2,line.getMenu().getItemId());
                insertDetailStmt.setInt(3,line.getQuantity());
                insertDetailStmt.setInt(4, 0);
                int registered_detail = insertDetailStmt.executeUpdate();

                if (registered_detail == 0){
                    throw new SQLException("注文登録失敗:itemId=" + line.getMenu().getItemId());
                }
            }
            con.commit();

            return new Order.Builder()
                    .orderId(orderId)
                    .orderDate(preOrder.getOrderDate())
                    .status(0)
                    .itemMap(preOrder.asMap())
                    .build();
        }catch(SQLException e){
            try{
                con.rollback();
                System.err.println("[ロールバック]" + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.err.println("[ロールバック失敗]" + rollbackEx.getMessage());
            }
            return null;
        }finally{
            try{
                con.setAutoCommit(true);
            }catch(SQLException e) {
                System.out.println("[エラー]"+e.getMessage());
            }
        }
    }

    // 在庫補充をDBに反映
    public boolean restockMenuItem(int itemId, int quantity){
        boolean isOk = true;
        String updateMenuSql = "UPDATE menu set stockQuantity = stockQuantity + ? WHERE itemId = ?";
        try(
            PreparedStatement updateMenuStmt = con.prepareStatement(updateMenuSql)
        ){
            updateMenuStmt.setInt(1,quantity);
            updateMenuStmt.setInt(2,itemId);
            int updated = updateMenuStmt.executeUpdate();

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
        String sql = "INSERT INTO menu(itemName, price, stockQuantity, category) VALUES (?, ?, ?, ?)";
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
        String updateDetailSql = "UPDATE order_detail SET status = ? WHERE orderId = ? AND itemId = ?";
        String selectDetailSql = "SELECT status FROM order_detail WHERE orderId = ?";
        String selectHeaderSql = "SELECT status FROM order_header WHERE orderId = ?";
        String updateHeaderSql = "UPDATE order_header SET status = ? WHERE orderId = ?";
        try(
            PreparedStatement updateDetailStmt = con.prepareStatement(updateDetailSql);
            PreparedStatement selectDetailStmt = con.prepareStatement(selectDetailSql);
            PreparedStatement selectHeaderStmt = con.prepareStatement(selectHeaderSql);
            PreparedStatement updateHeaderStmt = con.prepareStatement(updateHeaderSql)
        ){
            con.setAutoCommit(false); // rollback可能状態へ変更
            updateDetailStmt.setInt(1,status);
            updateDetailStmt.setInt(2,orderId);
            updateDetailStmt.setInt(3,itemId);
            updateDetailStmt.executeUpdate();

            selectDetailStmt.setInt(1,orderId);
            ResultSet rsD = selectDetailStmt.executeQuery();

            boolean allEq = true;
            int rs_start=0;
            if(rsD.next()){
                rs_start = rsD.getInt("status");
                while(rsD.next()){
                    if(rs_start != rsD.getInt("status")){
                        allEq = false;
                        break;
                    }
                }
            }
            if(allEq){
                selectHeaderStmt.setInt(1,orderId);
                ResultSet rsH = selectHeaderStmt.executeQuery();
                if(rsH.next()){
                    int rsH_status = rsH.getInt("status");
                    int rsD_status = rs_start;
                    // Headerのstatusが古かったら更新する
                    if(rsH_status < rsD_status){
                        updateHeaderStmt.setInt(1,rsD_status);
                        updateHeaderStmt.setInt(2,orderId);
                        updateHeaderStmt.executeUpdate();
                    }
                }
            }
        }catch(SQLException e){
            try{
                con.rollback();
                System.err.println("[ロールバック]" + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.err.println("[ロールバック失敗]" + rollbackEx.getMessage());
            }
            System.err.println("[エラー]:"+e.getMessage());
        }finally{
            try{
                con.setAutoCommit(true);
            }catch(SQLException e) {
                System.out.println("[エラー]"+e.getMessage());
            }
        }
    }

    public void updateStatusAll(int orderId, int status) {
        String updateDetailSql = "UPDATE order_detail SET status = ? WHERE orderId = ?";
        String updateHeaderSql = "UPDATE order_header SET status = ? WHERE orderId = ?";
        try (
            PreparedStatement updateDetailStmt = con.prepareStatement(updateDetailSql);
            PreparedStatement updateHeaderStmt = con.prepareStatement(updateHeaderSql)
        ) {
            con.setAutoCommit(false);

            // 明細すべてのステータスを更新
            updateDetailStmt.setInt(1, status);
            updateDetailStmt.setInt(2, orderId);
            int updatedDetailRows = updateDetailStmt.executeUpdate();

            if (updatedDetailRows == 0) {
                throw new SQLException("明細ステータスの更新に失敗しました（対象が存在しない可能性）");
            }

            // ヘッダーのステータスも更新
            updateHeaderStmt.setInt(1, status);
            updateHeaderStmt.setInt(2, orderId);
            updateHeaderStmt.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
                System.err.println("[ロールバック] " + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.err.println("[ロールバック失敗] " + rollbackEx.getMessage());
            }
            System.err.println("[エラー] " + e.getMessage());
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("[エラー] " + e.getMessage());
            }
        }
    }

}