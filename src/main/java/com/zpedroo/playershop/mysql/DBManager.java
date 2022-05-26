package com.zpedroo.playershop.mysql;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.objects.Shop;
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

public class DBManager extends ShopManager {

    public void saveShop(Shop shop) {
        executeUpdate("REPLACE INTO `" + DBConnection.TABLE + "` (`location`, `uuid`, `item`, `currency`, `buy_price`, `sell_price`, `amount`, `type`, `chest`, `display`) VALUES " +
                "('" + serializeLocation(shop.getLocation()) + "', " +
                "'" + shop.getOwnerUUID().toString() + "', " +
                "'" + itemStackArrayToBase64(new ItemStack[]{ shop.getItem() }) + "', " +
                "'" + shop.getCurrency().getFileName() + "', " +
                "'" + shop.getBuyPrice().toString() + "', " +
                "'" + shop.getSellPrice().toString() + "', " +
                "'" + shop.getDefaultAmount().toString() + "', " +
                "'" + shop.getType().toString() + "', " +
                "'" + toBase64(shop.getChestInventory()) + "', " +
                "'" + shop.getDisplay().toString() + "');");
    }

    public void deleteShop(Location location) {
        executeUpdate("DELETE FROM `" + DBConnection.TABLE + "` WHERE `location`='" + serializeLocation(location) + "';");
    }

    public HashMap<Location, Shop> getShops() {
        HashMap<Location, Shop> shops = new HashMap<>(128);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        String query = "SELECT * FROM `" + DBConnection.TABLE + "`;";

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeQuery();

            while (result.next()) {
                Location location = deserializeLocation(result.getString(1));
                UUID ownerUUID = UUID.fromString(result.getString(2));
                ItemStack item = Base64Encoder.itemStackArrayFromBase64(result.getString(3))[0];
                Currency currency = CurrencyAPI.getCurrency(result.getString(4));
                BigDecimal buyPrice = result.getBigDecimal(5);
                BigDecimal sellPrice = result.getBigDecimal(6);
                Integer amount = result.getInt(7);
                ShopType type = ShopType.valueOf(result.getString(8));
                Inventory chest = Base64Encoder.fromBase64(result.getString(9));
                Material display = Material.valueOf(result.getString(10));

                shops.put(location, new Shop(location, ownerUUID, item, currency, buyPrice.toBigInteger(), sellPrice.toBigInteger(), amount, type, chest, display));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnections(connection, result, preparedStatement, null);
        }

        return shops;
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
            closeConnections(connection, null, null, statement);
        }
    }

    private void closeConnections(Connection connection, ResultSet resultSet, PreparedStatement preparedStatement, Statement statement) {
        try {
            if (connection != null) connection.close();
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (statement != null) statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS `" + DBConnection.TABLE + "` (`location` VARCHAR(255), `uuid` VARCHAR(255), `item` LONGTEXT, `currency` VARCHAR(16), `buy_price` DECIMAL(40,0) NOT NULL, `sell_price` DECIMAL(40,0) NOT NULL, `amount` INTEGER NOT NULL, `type` VARCHAR(255) NOT NULL, `chest` LONGTEXT NOT NULL, `display` VARCHAR(255) NOT NULL, PRIMARY KEY(`location`));";
        executeUpdate(query);
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }
}