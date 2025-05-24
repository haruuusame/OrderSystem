package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // ======= select =======

    // 全カタログを取得
    public Optional<MenuCatalog> createMenuCatalogAll() {
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
            return Optional.of(new MenuCatalog(menus));
        }catch(SQLException e) {
            System.err.println("[エラー] " + e.getMessage());
        }
        return Optional.empty();
    }

    // カテゴリ指定でカタログ取得
    public Optional<MenuCatalog> createMenuCatalog(String category) {
        List<Menu> menus = new ArrayList<>();
        try(
            PreparedStatement pstmt = con.prepareStatement("SELECT itemId,itemName,price,stockQuantity FROM menu WHERE category = ?")
        ){
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                int itemId = rs.getInt("itemId");
                String itemName = rs.getString("itemName");
                int price = rs.getInt("price");
                int stockQuantity = rs.getInt("stockQuantity");
                menus.add(new Menu(itemId,itemName,price,stockQuantity,category));
            }
            rs.close();
            return Optional.of(new MenuCatalog(menus));
        }catch(SQLException e) {
            System.err.println("[エラー] " + e.getMessage());
        }
        return Optional.empty();
    }

    // メニューidからメニューを取得
    public Optional<Menu> fetchMenuById(int itemId){
        String menuSql = "SELECT * FROM menu WHERE itemId = ?";
        try(
        PreparedStatement menuStmt = con.prepareStatement(menuSql);
        ){
            menuStmt.setInt(1, itemId);
            ResultSet rs = menuStmt.executeQuery();
            if(!rs.next()) return Optional.empty();
            String itemName = rs.getString("itemName");
            int price = rs.getInt("price");
            int stockQuantity = rs.getInt("stockQuantity");
            String category = rs.getString("category");
            return Optional.of(new Menu(itemId,itemName,price,stockQuantity,category));
        }catch(SQLException e) {
            System.err.println("[エラー] " + e.getMessage());
        }
        return Optional.empty();
    }

    // 注文idから注文を取得
    public Optional<Order> fetchOrderById(int orderId){
        String headerSql = "SELECT * FROM order_header WHERE orderId = ?";
        String detailSql = "SELECT * FROM order_detail JOIN menu ON order_detail.itemId = menu.itemId WHERE orderId = ?";
        Map<Integer,OrderLine> itemMap = new LinkedHashMap<>();
        try(
            PreparedStatement headerStmt = con.prepareStatement(headerSql);
            PreparedStatement detailStmt = con.prepareStatement(detailSql)
        )
        {
            // ヘッダ情報の取得
            headerStmt.setInt(1,orderId);
            ResultSet rsH = headerStmt.executeQuery();

            if(!rsH.next()) return Optional.empty();

            LocalDateTime orderDate = LocalDateTime.parse(rsH.getString("orderDate"));
            int status = rsH.getInt("status");

            rsH.close();
            // 詳細情報の取得(menuテーブルとの結合)

            detailStmt.setInt(1,orderId);
            ResultSet rsD = detailStmt.executeQuery();

            while(rsD.next()){
                int itemId = rsD.getInt("itemId");
                int quantity = rsD.getInt("quantity");
                String itemName = rsD.getString("itemName");
                int price = rsD.getInt("price");
                int stockQuantity = rsD.getInt("stockQuantity");
                String category = rsD.getString("category");
                Menu menu = new Menu(itemId,itemName,price,stockQuantity,category);
                itemMap.put(itemId,new OrderLine(menu,quantity));
            }
            rsD.close();

            return Optional.of(new Order(itemMap,orderId,orderDate,status));
        }catch(SQLException e){
            System.err.println("[エラー] "+e.getMessage());
            return Optional.empty();
        }
    }

    // 条件付きで注文を取得
    private List<Order> fetchOrders(String conditionSql,List<Object> params){
        String headerSql = "SELECT * FROM order_header WHERE " + conditionSql;
        Map<Integer,Order.Builder> orderBuilders = new LinkedHashMap<>();
        Map<Integer,Menu> menuCache = new HashMap<>();
        try(
            PreparedStatement headerStmt = con.prepareStatement(headerSql)
        )
        {
            // ヘッダ情報の取得

            // sqlの形成
            int i = 1;
            for(Object param:params){
                if(param instanceof Integer){
                    headerStmt.setInt(i++,(Integer)param);
                }else if(param instanceof LocalDateTime){
                    headerStmt.setString(i++,param.toString());
                }else{
                    headerStmt.setString(i++,param.toString());
                }
            }
            
            ResultSet rsH = headerStmt.executeQuery();

            while(rsH.next()){
                int orderId = rsH.getInt("orderId");
                LocalDateTime orderDate = LocalDateTime.parse(rsH.getString("orderDate"));
                int status = rsH.getInt("status");
                Order.Builder ob = new Order.Builder().orderId(orderId).orderDate(orderDate).status(status);
                orderBuilders.put(orderId,ob);
            }

            if (orderBuilders.isEmpty()) return List.of();

            // 詳細情報の取得(menuテーブルとの結合)
            
            // SQL構文の組み立て(?の数をorderIdに依存して動的に制御)
            String placeholders = orderBuilders.keySet().stream().map(id -> "?").collect(Collectors.joining(","));
            String detailSql = "SELECT * FROM order_detail JOIN menu ON order_detail.itemId = menu.itemId WHERE orderId IN (" + placeholders + ")";
            
            // 詳細情報を取得する
            try(
                PreparedStatement detailStmt = con.prepareStatement(detailSql)
            ) 
            {
                // ?にset
                int index = 1;
                for(int orderId: orderBuilders.keySet()){
                    detailStmt.setInt(index++,orderId);
                }

                ResultSet rsD = detailStmt.executeQuery();

                while(rsD.next()){
                    // order_detail部分
                    int orderId = rsD.getInt("orderId");
                    int itemId = rsD.getInt("itemId");
                    int quantity = rsD.getInt("quantity");
                    // キャッシュを通じてitemIdからmenuを取得
                    Menu menu = menuCache.computeIfAbsent(itemId,id->{
                        // キャッシュに存在しないときは取得する
                        try{
                            String itemName = rsD.getString("itemName");
                            int price = rsD.getInt("price");
                            int stockQuantity = rsD.getInt("stockQuantity");
                            String category = rsD.getString("category");
                            return new Menu(itemId,itemName,price,stockQuantity,category);
                        }catch(SQLException e){
                            throw new RuntimeException(e);
                        }
                    });

                    // OrderのBuilderにOrderLineを登録
                    OrderLine ol = new OrderLine(menu, quantity);
                    orderBuilders.get(orderId).add(ol);
                }
            
            }catch(SQLException e){
                throw new RuntimeException(e);
            }

            List<Order> orders = orderBuilders.values().stream()
                .map(Order.Builder::build)
                .toList();
            
            return orders;
        }catch(SQLException e){
            System.err.println("[エラー] "+e.getMessage());
            return List.of();
        }
    }

    // ステータスから注文を取得
    public List<Order> fetchOrdersByStatus(int status){
        return fetchOrders("status = ?", List.of(status));
    }

    // 日付から注文を取得
    public List<Order> fetchOrdersByDateTimeRange(LocalDateTime from,LocalDateTime to){
        return fetchOrders("orderDate BETWEEN ? AND ?",List.of(from,to));
    }

    // ステータスと日付から注文を取得
    public List<Order> fetchOrdersByStatusAndDateTime(int status, LocalDateTime from, LocalDateTime to) {
        return fetchOrders("status = ? AND orderDate BETWEEN ? AND ?", List.of(status, from, to));
    }

    // ======= insert/update =======

    // 注文をDBに登録
    public Optional<Order> registerOrder(Order.Builder preOrder){
        String insertHeaderSql = "INSERT INTO order_header(orderDate,status) VALUES (?, ?)";
        String insertDetailSql = "INSERT INTO order_detail(orderId,itemId,quantity,status) VALUES (?, ?, ?, ?)";
        String updateMenuSql = "UPDATE menu SET stockQuantity = stockQuantity - ? WHERE itemId = ? AND stockQuantity >= ?";
        LocalDateTime orderdDateTime = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        try(
            PreparedStatement insertHeaderStmt = con.prepareStatement(insertHeaderSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertDetailStmt = con.prepareStatement(insertDetailSql);
            PreparedStatement updateMenuStmt = con.prepareStatement(updateMenuSql)
        ){
            con.setAutoCommit(false); // rollback可能状態へ変更
            // 注文の概要をheaderテーブルへ追加
            insertHeaderStmt.setString(1,orderdDateTime.toString());
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

            return Optional.of(
                preOrder
                    .orderId(orderId)
                    .orderDate(orderdDateTime)
                    .build()
                );
        }catch(SQLException e){
            try{
                con.rollback();
                System.err.println("[ロールバック]" + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.err.println("[ロールバック失敗]" + rollbackEx.getMessage());
            }
            return Optional.empty();
        }finally{
            try{
                con.setAutoCommit(true);
            }catch(SQLException e) {
                System.out.println("[エラー] "+e.getMessage());
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
            System.err.println("[エラー] "+e.getMessage());
        }
        return isOk;
    }

    // メニューを新規作成
    public Optional<Menu> addNewMenuItem(String itemName,int price,int stockQuantity,String category){
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
                return Optional.of(new Menu(itemId,itemName,price,stockQuantity,category));
            }else{
                throw new SQLException("itemIdの生成に失敗しました");
            }
        }catch(SQLException e){
            System.err.println("[エラー] "+e.getMessage());
            return Optional.empty();
        }
    }

    // ステータスを更新
    public void updateStatus(int orderId,int itemId,int status){
        String updateDetailSql = "UPDATE order_detail SET status = ? WHERE orderId = ? AND itemId = ?";
        try(
            PreparedStatement updateDetailStmt = con.prepareStatement(updateDetailSql)
        ){
            updateDetailStmt.setInt(1,status);
            updateDetailStmt.setInt(2,orderId);
            updateDetailStmt.setInt(3,itemId);
            updateDetailStmt.executeUpdate();
            
        }catch(SQLException e){
            System.err.println("[エラー] "+e.getMessage());
        }finally{
            try{
                con.setAutoCommit(true);
            }catch(SQLException e) {
                System.out.println("[エラー] "+e.getMessage());
            }
        }
    }

    // orderIdに属するすべてのstatusを更新する
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