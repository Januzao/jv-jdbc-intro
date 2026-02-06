package mate.academy.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Book;
import mate.academy.util.ConnectionUtil;

@Dao
public class BookDaoImpl implements BookDao {
    @Override
    public Book create(Book book) {
        String sqlQuery = "insert into books (title, price) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setBigDecimal(2, book.getPrice());
            int effectedRows = preparedStatement.executeUpdate();
            if (effectedRows < 1) {
                throw new RuntimeException("Expected at least one row, but inserted 0 rows");
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                book.setId(id);
            }
            return book;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot add new book: ", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sqlQuery = "select * from books where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String title = resultSet.getString("title");
                BigDecimal price = resultSet.getBigDecimal("price");
                Book book = new Book();
                book.setId(id);
                book.setTitle(title);
                book.setPrice(price);
                return Optional.of(book);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find a book by id: ", e);
        }
    }

    @Override
    public List<Book> findAll() {
        String sqlQuery = "select * from books";
        List<Book> books = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                BigDecimal price = resultSet.getBigDecimal("price");
                Book book = new Book();
                book.setId(id);
                book.setTitle(title);
                book.setPrice(price);
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find data in schema", e);
        }
    }

    @Override
    public Book update(Book book) {
        String sqlQuery = "update books set title = ?, price = ? where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setBigDecimal(2, book.getPrice());
            preparedStatement.setLong(3, book.getId());
            int effectedRows = preparedStatement.executeUpdate();
            if (effectedRows < 1) {
                throw new RuntimeException("Expected at least one row, but inserted 0 rows");
            }
            return book;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update info about book." + book.getId(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sqlQuery = "delete from books where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setLong(1, id);
            int effectedRows = preparedStatement.executeUpdate();
            return effectedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete book by id: ", e);
        }
    }
}
