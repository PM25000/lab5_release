package system;

import entities.Book;
import entities.Borrow;
import entities.Card;
import utils.DBInitializer;
import utils.DatabaseConnector;
import queries.ApiResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import queries.BorrowHistories;
import queries.BookQueryConditions;
import queries.BookQueryResults;
import queries.SortOrder;
import queries.CardList;
public class LibraryManagementSystemImpl implements LibraryManagementSystem {

    private final DatabaseConnector connector;

    public LibraryManagementSystemImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public ApiResult storeBook(Book book) {

                try (PreparedStatement ps = connector.getConn().prepareStatement("insert ignore into book values(?,?,?,?,?,?,?,?)")) {
                    if(book.getBookId()==0)
                        ps.setNull(1,Types.INTEGER);
                    else
                        ps.setInt(1, book.getBookId());
                    ps.setString(2, book.getCategory());
                    ps.setString(3, book.getTitle());
                    ps.setString(4, book.getPress());
                    ps.setInt(5, book.getPublishYear());
                    ps.setString(6, book.getAuthor());
                    ps.setDouble(7, book.getPrice());
                    ps.setInt(8, book.getStock());
                    if(ps.executeUpdate()>0) {
                        PreparedStatement ps1 = connector.getConn().prepareStatement("select book_id from book where category=? and title=? and press=? and publish_year=? and author=?");
                        ps1.setString(1, book.getCategory());
                        ps1.setString(2, book.getTitle());
                        ps1.setString(3, book.getPress());
                        ps1.setInt(4, book.getPublishYear());
                        ps1.setString(5, book.getAuthor());
                        ResultSet rs1 = ps1.executeQuery();
                        rs1.next();
                        int bookId = rs1.getInt("book_id");
                        book.setBookId(bookId);
                        commit(connector.getConn());
                        return new ApiResult(true, "success insert");
                    }
                }
                catch(SQLException e ){
                    e.printStackTrace();

                }

        return new ApiResult(false, "fail");

    }







    @Override
    public ApiResult incBookStock(int bookId, int deltaStock) {
        try(PreparedStatement ps=connector.getConn().prepareStatement("select * from book where book_id=?" )){
            ps.setInt(1,bookId);
            ResultSet rs=ps.executeQuery();
            if(!rs.next()){
                return new ApiResult(false, "fail");
            }
            else{
            if(rs.getInt("stock")+deltaStock<0){
                return new ApiResult(false, "fail");
            }
            else{
                try(PreparedStatement ps1=connector.getConn().prepareStatement("update book set stock=stock+? where book_id=?" )){
                    ps1.setInt(1,deltaStock);
                    ps1.setInt(2,bookId);

                    if(ps1.executeUpdate()>0){
                        commit(connector.getConn());
                        return new ApiResult(true, "success");
                    }
                }
            }
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }
        return new ApiResult(false, "fail");
    }

    @Override
    public ApiResult storeBook(List<Book> books) {
        int flag=0;
        for(int i=0;i<books.size();i++){
            for(int j=i+1;j<books.size();j++){
                if(books.get(i).equals(books.get(j))){
                    flag=1;
                }
            }
        }
        if(flag==1){
            return new ApiResult(false, "fail");
        }
        for(int i=0;i< books.size();i++){
            try (PreparedStatement ps = connector.getConn().prepareStatement("insert ignore into book values(?,?,?,?,?,?,?,?)")) {
                if(books.get(i).getBookId()==0)
                    ps.setNull(1,Types.INTEGER);
                else
                    ps.setInt(1, books.get(i).getBookId());
                ps.setString(2, books.get(i).getCategory());
                ps.setString(3, books.get(i).getTitle());
                ps.setString(4, books.get(i).getPress());
                ps.setInt(5, books.get(i).getPublishYear());
                ps.setString(6, books.get(i).getAuthor());
                ps.setDouble(7, books.get(i).getPrice());
                ps.setInt(8, books.get(i).getStock());
                if(ps.executeUpdate()==0) {
                    rollback(connector.getConn());
                    return new ApiResult(false, "fail");
                }
                PreparedStatement ps1 = connector.getConn().prepareStatement("select book_id from book where category=? and title=? and press=? and publish_year=? and author=?");
                ps1.setString(1, books.get(i).getCategory());
                ps1.setString(2, books.get(i).getTitle());
                ps1.setString(3, books.get(i).getPress());
                ps1.setInt(4, books.get(i).getPublishYear());
                ps1.setString(5, books.get(i).getAuthor());
                ResultSet rs1 = ps1.executeQuery();
                rs1.next();
                int bookId = rs1.getInt("book_id");
                books.get(i).setBookId(bookId);
            }
            catch(SQLException e ){
                e.printStackTrace();
            }
        }
        commit(connector.getConn());
        return new ApiResult(true, "success");
    }

    @Override
    public ApiResult removeBook(int bookId) {
        try(PreparedStatement ps2 = connector.getConn().prepareStatement("select * from borrow where book_id= ?")){
            ps2.setInt(1,bookId);
            ResultSet rs2= ps2.executeQuery();
            while(rs2.next()){
                if(rs2.getLong("return_time")==0){
                    return new ApiResult(false, "fail");
                }
            }}
        catch(SQLException e ){
            e.printStackTrace();
        }

        try(PreparedStatement ps = connector.getConn().prepareStatement("select * from book where book_id = ?")) {
            ps.setInt(1,bookId);
            ResultSet rs=ps.executeQuery();
            while(!rs.next()){
                return new ApiResult(false, "fail");
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }
        try(PreparedStatement ps1 = connector.getConn().prepareStatement("delete from book where book_id=?")){
            ps1.setInt(1,bookId);
            ps1.executeUpdate();
        }
        catch(SQLException e ){
            e.printStackTrace();

    }
        commit(connector.getConn());
        return new ApiResult(true, "success");
    }
    @Override
    public ApiResult modifyBookInfo(Book book) {
        try(PreparedStatement ps = connector.getConn().prepareStatement("update book set category=?,title=?,press=?,publish_year=?,author=?,price=? where book_id=?")){
            ps.setString(1,book.getCategory());
            ps.setString(2,book.getTitle());
            ps.setString(3,book.getPress());
            ps.setInt(4,book.getPublishYear());
            ps.setString(5,book.getAuthor());
            ps.setDouble(6,book.getPrice());
            ps.setInt(7,book.getBookId());
            if(ps.executeUpdate()==0){
                return new ApiResult(false, "fail");
            }

        }
        catch(SQLException e ){
            e.printStackTrace();
            return new ApiResult(false, "fail");
        }

        commit(connector.getConn());
        return new ApiResult(true, "success");
    }

    @Override
    public ApiResult queryBook(BookQueryConditions conditions) {
        try(Statement ps=connector.getConn().createStatement()){
            String s,s1,s2,s3,s4,s5,s6,s7,s8;
            if(conditions.getCategory()!=null){
                s1="category = '" + conditions.getCategory()+"'";
            }
             else{
                    s1="1=1";
                }
            if(conditions.getTitle()!=null){
                  s2="title like '%"+conditions.getTitle()+"%'";
            }
            else{
                s2="1=1";
            }
            if(conditions.getMinPublishYear()!=null){
                s3="publish_year>="+conditions.getMinPublishYear().toString();
            }
            else{
                s3="1=1";
            }
            if(conditions.getMaxPublishYear()!=null){
                s4="publish_year<="+conditions.getMaxPublishYear().toString();
            }
            else{
                s4="1=1";
            }
            if(conditions.getPress()!=null ){
                s5="press like'%"+conditions.getPress()+"%'";
            }
            else{
               s5="1=1";
            }
            if(conditions.getMinPrice()!=null){
                s6="price>="+conditions.getMinPrice().toString();
            }
            else{
                s6="1=1";
            }
            if(conditions.getMaxPrice()!=null){
                s7="price<="+conditions.getMaxPrice().toString();
            }
            else{
                s7="1=1";
            }
            if(conditions.getAuthor()!=null){
                s8="author like '%" + conditions.getAuthor()+"%'";
            }
            else{
                s8="1=1";
            }
            s="select * from book where " + s1 + " and "+s2+" and "+s3+" and "+s4+" and "+s5+" and "+s6+" and "+s7 + " and "+s8;
            //ResultSet rs=ps.executeQuery(s);

            System.out.println(s);
            ResultSet rs=ps.executeQuery(s);
           // if(!rs.next()){
             //   return new ApiResult(false, "fail");
            //}
            List<Book> ResultList = new ArrayList<Book>();

            while(rs.next()){
                Book newbook= new Book();
                newbook.setBookId(rs.getInt("book_id"));
                newbook.setCategory(rs.getString("category"));
                newbook.setTitle(rs.getString("title"));
                newbook.setPress(rs.getString("press"));
                newbook.setPublishYear(rs.getInt("publish_year"));
                newbook.setAuthor(rs.getString("author"));
                newbook.setPrice(rs.getDouble("price"));
                newbook.setStock(rs.getInt("stock"));
                ResultList.add(newbook);

            }
            Comparator<Book> cmp = conditions.getSortBy().getComparator();
            if (conditions.getSortOrder() == SortOrder.DESC) {
                cmp = cmp.reversed();
            }
            Comparator<Book> comparator = cmp;
           // Comparator<Book> sortComparator = (lhs, rhs) -> {
               // if (comparator.compare(lhs, rhs) == 0) {
                   // return lhs.getBookId() - rhs.getBookId();
              //  }
              ///  return comparator.compare(lhs, rhs);
            //};
            Comparator<Book> sortComparator = cmp.thenComparingInt(Book::getBookId);
            //return stream.sorted(sortComparator).collect(Collectors.toCollection(LinkedList::new))
            ResultList.sort(sortComparator);
            BookQueryResults Results=new BookQueryResults(ResultList);
            commit(connector.getConn());
            return new ApiResult(true, "success ",Results);
        }
        catch(SQLException e ){
            e.printStackTrace();
        }




        return new ApiResult(false, "fail");
    }

    @Override
    public ApiResult borrowBook(Borrow borrow) {
        try (PreparedStatement ps2 = connector.getConn().prepareStatement("select * from card where card_id=?")){
            ps2.setInt(1,borrow.getCardId());
            ResultSet rs2= ps2.executeQuery();
            if(!rs2.next()){
                return new ApiResult(false, "fail1");
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }
        try(PreparedStatement ps3 = connector.getConn().prepareStatement("select * from borrow where book_id=? and card_id=?")){
            ps3.setInt(1,borrow.getBookId());
            ps3.setInt(2,borrow.getCardId());
            ResultSet rs3=ps3.executeQuery();
            while(rs3.next()){
                if(rs3.getLong("return_time")==0){
                    //System.out.println("here1");
                    return new ApiResult(false, "fail2");
                }
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }
        try (PreparedStatement ps1 = connector.getConn().prepareStatement("select * from book where book_id=?")){
            ps1.setInt(1,borrow.getBookId());
            ResultSet rs1= ps1.executeQuery();
            if(!rs1.next()){
                //System.out.println("here2");
                return new ApiResult(false, "fail3");
            }
            if(rs1.getInt("stock")==0){
               // System.out.println("here3");
                return new ApiResult(false, "fail4");
            }
            try (PreparedStatement ps4 = connector.getConn().prepareStatement("update book set stock =? where book_id=?")){
                ps4.setInt(1,rs1.getInt("stock")-1);
                ps4.setInt(2,rs1.getInt("book_id"));
                ps4.executeUpdate();
            }

        }
        catch(SQLException e ){
            e.printStackTrace();
        }

        try (PreparedStatement ps = connector.getConn().prepareStatement("insert ignore into borrow values(?,?,?,?)")) {
            borrow.resetBorrowTime();
            //borrow.setReturnTime(0);
            ps.setInt(1, borrow.getCardId());
            ps.setInt(2, borrow.getBookId());
            ps.setLong(3, borrow.getBorrowTime());
            ps.setLong(4, 0);
           // System.out.println(borrow.toString());
            if(ps.executeUpdate()>0) {
                try (PreparedStatement ps2 = connector.getConn().prepareStatement("select  count(*) from borrow")){
                    ResultSet rs2= ps2.executeQuery();
                    int count=0;
                    while (rs2.next()){
                        count++;
                       // System.out.println (rs2.getInt(1));
                    }
                }
                commit(connector.getConn());
                return new ApiResult(true, "success insert");
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }

        System.out.println("here4");
        return new ApiResult(false, "fail5");


    }

    @Override
    public ApiResult returnBook(Borrow borrow) {
        try (PreparedStatement ps2 = connector.getConn().prepareStatement("select * from borrow where card_id=? and book_id=? and borrow_time=? ")){
            ps2.setInt(1,borrow.getCardId());
            ps2.setInt(2,borrow.getBookId());
            ps2.setLong(3,borrow.getBorrowTime());
            ResultSet rs2= ps2.executeQuery();
            if(!rs2.next()){
                return new ApiResult(false, "fail");
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }
        try (PreparedStatement ps3 = connector.getConn().prepareStatement("update borrow set return_time=? where card_id=? and book_id=? and borrow_time=? ")){
            borrow.resetReturnTime();
            ps3.setLong(1,borrow.getReturnTime());
            ps3.setInt(2,borrow.getCardId());
            ps3.setInt(3,borrow.getBookId());
            ps3.setLong(4,borrow.getBorrowTime());
            ps3.executeUpdate();
        }
        catch(SQLException e ){
            e.printStackTrace();
        }
        try (PreparedStatement ps1 = connector.getConn().prepareStatement("select * from book where book_id=?")){
            ps1.setInt(1,borrow.getBookId());
            ResultSet rs1= ps1.executeQuery();
            if(!rs1.next()){
                return new ApiResult(false, "fail");
            }

            try (PreparedStatement ps4 = connector.getConn().prepareStatement("update book set stock =? where book_id=?")){
                ps4.setInt(1,rs1.getInt("stock")+1);
                ps4.setInt(2,rs1.getInt("book_id"));
                ps4.executeUpdate();
            }
            commit(connector.getConn());
            return new ApiResult(true, "success");
        }
        catch(SQLException e ){
            e.printStackTrace();
        }




        return new ApiResult(false, "fail");


    }

    @Override
    public ApiResult showBorrowHistory(int cardId) {
        List<BorrowHistories.Item> resultlist = new ArrayList<BorrowHistories.Item>();
        try (PreparedStatement ps = connector.getConn().prepareStatement("select * from borrow where card_id=? ")) {
            //List<BorrowHistories.Item> resultlist = new ArrayList<BorrowHistories.Item>();
            ps.setInt(1, cardId);
            ResultSet rs = ps.executeQuery();
            //rs.next();
            //System.out.println (rs.getInt(1));
           // int count=0;
            while (rs.next()) {
             //   count++;
               // System.out.println (count);
                Book newbook = new Book();
                Borrow newborrow = new Borrow();
                newborrow.setBookId(rs.getInt("book_id"));
                newborrow.setCardId(rs.getInt("card_id"));
                newborrow.setBorrowTime(rs.getLong("borrow_time"));
                newborrow.setReturnTime(rs.getLong("return_time"));
                try (PreparedStatement ps1 = connector.getConn().prepareStatement("select * from book where book_id=?")) {
                    ps1.setInt(1, rs.getInt("book_id"));
                    ResultSet rs2 = ps1.executeQuery();
                    while (rs2.next()) {
                        newbook.setBookId(rs2.getInt("book_id"));
                        newbook.setCategory(rs2.getString("category"));
                        newbook.setTitle(rs2.getString("title"));
                        newbook.setPress(rs2.getString("press"));
                        newbook.setPublishYear(rs2.getInt("publish_year"));
                        newbook.setAuthor(rs2.getString("author"));
                        newbook.setPrice(rs2.getDouble("price"));
                        newbook.setStock(rs2.getInt("stock") - 1);
                        BorrowHistories.Item newitem = new BorrowHistories.Item(cardId, newbook, newborrow);
                        resultlist.add(newitem);
                    }
                }


            }
            Comparator<BorrowHistories.Item> byBorrowTime = Comparator.comparing(BorrowHistories.Item::getBorrowTime).reversed();
            Comparator<BorrowHistories.Item> byId = Comparator.comparing(BorrowHistories.Item::getBookId);
            Comparator<BorrowHistories.Item> byBorrowTimeAndId = byBorrowTime.thenComparing(byId);
            resultlist.sort(byBorrowTimeAndId);
            BorrowHistories result=new BorrowHistories(resultlist);
            commit(connector.getConn());
            return new ApiResult(true, "success",result);
        }catch(SQLException e ){
            e.printStackTrace();
        }
        return new ApiResult(false, "fail");
    }

    @Override
    public ApiResult registerCard(Card card) {
        try (PreparedStatement ps = connector.getConn().prepareStatement("insert ignore into card values(?,?,?,?)")) {
            if(card.getCardId()==0)
                ps.setNull(1,Types.INTEGER);
            else
                ps.setInt(1, card.getCardId());
            ps.setString(2, card.getName());
            ps.setString(3, card.getDepartment());
            ps.setString(4, card.getType().getStr());
            if(ps.executeUpdate()>0) {

                PreparedStatement ps1 = connector.getConn().prepareStatement("select card_id from card where name=? and department=? and type=?");

                ps1.setString(1, card.getName());
                ps1.setString(2, card.getDepartment());
                ps1.setString(3, card.getType().getStr());

                ResultSet res = ps1.executeQuery();
                res.next();
                int id = res.getInt("card_id");
                card.setCardId(id);

                commit(connector.getConn());
                return new ApiResult(true, "success insert");
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }


        return new ApiResult(false, "fail");

    }

    @Override
    public ApiResult removeCard(int cardId) {
        try(PreparedStatement ps2 = connector.getConn().prepareStatement("select * from borrow where card_id = ?")){
            ps2.setInt(1,cardId);
            ResultSet rs2= ps2.executeQuery();
            while(rs2.next()){
                if(rs2.getLong("return_time")==0){
                    return new ApiResult(false, "fail");
            }
        }}
        catch(SQLException e ){
            e.printStackTrace();
        }

        try(PreparedStatement ps = connector.getConn().prepareStatement("select * from card where card_id = ?")) {
            ps.setInt(1,cardId);
            ResultSet rs=ps.executeQuery();
            if(!rs.next()){
                //System.out.println ("this is"+cardId);
                return new ApiResult(false, "fail");
            }
            try(PreparedStatement ps1 = connector.getConn().prepareStatement("delete from card where card_id=?")){
                ps1.setInt(1,cardId);
                ps1.executeUpdate();
                commit(connector.getConn());
                return new ApiResult(true, "success");
            }
        }
        catch(SQLException e ){
            e.printStackTrace();
        }


        return new ApiResult(false, "fail");


    }

    @Override
    public ApiResult showCards() {
        try(PreparedStatement ps = connector.getConn().prepareStatement("select * from card")){
            ResultSet rs=ps.executeQuery();
            List<Card> ResultList = new ArrayList<Card>();

            while(rs.next()){
                Card newcard= new Card();
                newcard.setCardId(rs.getInt("card_id"));
                newcard.setName(rs.getString("name"));
                newcard.setDepartment(rs.getString("department"));
                String s=rs.getString("type");
                if(Objects.equals(s, "S")){
                    s="Student";
                }
                else{
                    s="Teacher";
                }
                Card.CardType c =Card.CardType.valueOf(s);
                newcard.setType(c);
                ResultList.add(newcard);

            }
           ResultList.sort(Comparator.comparingInt(Card::getCardId));
            CardList Results=new CardList(ResultList);
            commit(connector.getConn());
            return new ApiResult(true, "success",Results);
        }
        catch(SQLException e ){
            e.printStackTrace();
        }

        return new ApiResult(false, "fail");
    }

    @Override
    public ApiResult resetDatabase() {
        Connection conn = connector.getConn();
        try {
            Statement stmt = conn.createStatement();
            DBInitializer initializer = connector.getConf().getType().getDbInitializer();
            stmt.addBatch(initializer.sqlDropBorrow());
            stmt.addBatch(initializer.sqlDropBook());
            stmt.addBatch(initializer.sqlDropCard());
            stmt.addBatch(initializer.sqlCreateCard());
            stmt.addBatch(initializer.sqlCreateBook());
            stmt.addBatch(initializer.sqlCreateBorrow());
            stmt.executeBatch();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, null);
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commit(Connection conn) {
        try {
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
