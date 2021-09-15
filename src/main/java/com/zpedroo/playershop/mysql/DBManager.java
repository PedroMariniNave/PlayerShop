package com.zpedroo.playershop.mysql;

import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.shop.Shop;
import com.zpedroo.playershop.utils.encoder.Base64Encoder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

import static com.zpedroo.playershop.utils.encoder.Base64Encoder.*;

public class DBManager {

    private ShopManager manager;

    public DBManager() {
        this.manager = new ShopManager();
    }

    public void saveShop(Shop shop) {
        if (contains(getManager().serializeLocation(shop.getLocation()), "location")) {
            String query = "UPDATE `" + DBConnection.TABLE + "` SET" +
                    "`location`='" + getManager().serializeLocation(shop.getLocation()) + "', " +
                    "`uuid`='" + shop.getOwnerUUID().toString() + "', " +
                    "`item`='" + itemStackArrayToBase64(new ItemStack[]{ shop.getItem() }) + "', " +
                    "`buy_price`='" + shop.getBuyPrice().toString() + "', " +
                    "`sell_price`='" + shop.getSellPrice().toString() + "', " +
                    "`amount`='" + shop.getAmount().toString() + "', " +
                    "`type`='" + shop.getType().toString() + "', " +
                    "`chest`='" + toBase64(shop.getChest()) + "', " +
                    "`display`='" + shop.getDisplay().toString() + "' " +
                    "WHERE `location`='" + getManager().serializeLocation(shop.getLocation()) + "';";
            executeUpdate(query);
            return;
        }

        String query = "INSERT INTO `" + DBConnection.TABLE + "` (`location`, `uuid`, `item`, `buy_price`, `sell_price`, `amount`, `type`, `chest`, `display`) VALUES " +
                "('" + getManager().serializeLocation(shop.getLocation()) + "', " +
                "'" + shop.getOwnerUUID().toString() + "', " +
                "'" + itemStackArrayToBase64(new ItemStack[]{ shop.getItem() }) + "', " +
                "'" + shop.getBuyPrice().toString() + "', " +
                "'" + shop.getSellPrice().toString() + "', " +
                "'" + shop.getAmount().toString() + "', " +
                "'" + shop.getType().toString() + "', " +
                "'" + toBase64(shop.getChest()) + "', " +
                "'" + shop.getDisplay().toString() + "');";
        executeUpdate(query);
    }

    public void deleteShop(String location) {
        String query = "DELETE FROM `" + DBConnection.TABLE + "` WHERE `location`='" + location + "';";
        executeUpdate(query);
    }

    public HashMap<String, Shop> getShops() {
        HashMap<String, Shop> shops = new HashMap<>(5120);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT * FROM `" + DBConnection.TABLE + "`;";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            while (result.next()) {
                Location location = getManager().deserializeLocation(result.getString(1));
                UUID ownerUUID = UUID.fromString(result.getString(2));
                ItemStack item = Base64Encoder.itemStackArrayFromBase64(result.getString(3))[0];
                BigDecimal buyPrice = result.getBigDecimal(4);
                BigDecimal sellPrice = result.getBigDecimal(5);
                Integer amount = result.getInt(6);
                ShopType type = ShopType.valueOf(result.getString(7));
                Inventory chest = Base64Encoder.fromBase64(result.getString(8));
                Material display = Material.valueOf(result.getString(9));

                shops.put(getManager().serializeLocation(location), new Shop(location, ownerUUID, item, buyPrice.toBigInteger(), sellPrice.toBigInteger(), amount, type, chest, display));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, result, preparedStatement, null);
        }

        return shops;
    }

    private Boolean contains(String value, String column) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT `" + column + "` FROM `" + DBConnection.TABLE + "` WHERE `" + column + "`='" + value + "';";
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();
            return result.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, result, preparedStatement, null);
        }

        return false;
    }

    private void executeUpdate(String query) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(connection, null, null, statement);
        }
    }

    private void closeConnection(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement, Statement statement) {
        try {
            if (connection != null) connection.close();
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (statement != null) statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected void setupTable() {
        String query = "CREATE TABLE IF NOT EXISTS `" + DBConnection.TABLE + "` (`location` VARCHAR(255) NOT NULL, `uuid` VARCHAR(255) NOT NULL, `item` LONGTEXT NOT NULL, `buy_price` DECIMAL(40,0) NOT NULL, `sell_price` DECIMAL(40,0) NOT NULL, `amount` INTEGER NOT NULL, `type` VARCHAR(255) NOT NULL, `chest` LONGTEXT NOT NULL, `display` VARCHAR(255) NOT NULL, PRIMARY KEY(`location`));";
        executeUpdate(query);
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    private ShopManager getManager() {
        return manager;
    }
}