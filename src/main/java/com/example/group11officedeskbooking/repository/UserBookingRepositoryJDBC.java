package com.example.group11officedeskbooking.repository;

import com.example.group11officedeskbooking.DTO.*;
import com.example.group11officedeskbooking.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.ObjectError;

import java.awt.print.Book;
import java.util.List;
@Repository
public class UserBookingRepositoryJDBC implements UserBookingRepository{
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public UserBookingRepositoryJDBC(JdbcTemplate aTemplate){
        jdbcTemplate = aTemplate;
    }

    @Override
    //list DTO for all User booking records
    public List<UserBookingDTO> findBookingByUserId(int id) {

        // JdbcTemplate query used to get multiple records from the database

        return jdbcTemplate.query(
            "SELECT booking_id,booking_date,desk_location,Desk_desk_id, DATE_FORMAT(booking_date,'%D %M %Y') AS formattedDate FROM Booking JOIN Desk ON Booking.Desk_desk_id = Desk.desk_id WHERE Booking.User_user_id=? && Booking_date >= CURDATE() ORDER BY Booking_date",

                new UserBookingMapper(), new Object[]{id});

    }

    @Override
    public UserDTO findUserByUserID(int id){
        return (UserDTO) jdbcTemplate.queryForObject(
                "select * from user where user_id=?",
                new UserInfoMapper(),
                new Object[]{id});
    }

    @Override
    public boolean addBooking(int user_id, String date, int desk_id){
        int rows = jdbcTemplate.update(
                "insert into booking(booking_date, User_user_id, Desk_desk_id) values(?,?,?)",
                new Object[]{date, user_id, desk_id});
        return rows>0;
    }

    @Override
    public BookingDTO getUniqueBooking(String date, int desk_id){
        return (BookingDTO) jdbcTemplate.queryForObject(
                "SELECT Booking.booking_date, Desk.desk_number, Desk.desk_location from Booking Inner Join Desk ON Booking.Desk_desk_id = Desk.desk_id where booking_date=? and Desk_desk_id=?",
                new BookingMapper(),
                new Object[]{date, desk_id});
    }

    @Override
    public boolean checkLotteryDay(String date, String location){
        int rows = jdbcTemplate.queryForObject(
                "select count(*) from lottery where date=? and location=?",
                Integer.class, new Object[]{date, location});
        return rows > 0;
    }

    public boolean addUserToLottery(String date, String location, int user_id){
        int rows = jdbcTemplate.update(
                "insert into lottery(date, location, user_id) values(?, ?, ?)",
                new Object[]{date, location, user_id});
        return rows > 0;
    }

    @Override
    public boolean checkUserInLottery(String date, String location, int user_id){
        int rows =jdbcTemplate.queryForObject(
                "select count(*) from lottery where date=? and location=? and user_id=?",
                Integer.class, new Object[]{date, location, user_id}
        );
        return rows > 0;
    }

    @Override
    public int checkNumberInLottery(String date, String location){
        int rows = jdbcTemplate.queryForObject(
                "select count(*) from lottery where date=? and location=?",
                Integer.class, new Object[]{date, location});

        return rows - 1;
    }

    @Override
    public int checkNumberInLocation(String location){
        int rows = jdbcTemplate.queryForObject(
                "select count(*) from desk where desk_location=?",
                Integer.class, new Object[]{location});
        return rows;
    }

    @Override
    public boolean checkIfUserHasBooking(String date, int user_id){
        int rows = jdbcTemplate.queryForObject(
                "select count(*) from booking where booking_date=? and User_user_id=?",
                Integer.class, new Object[]{date, user_id});
        return rows > 0;
    }

    @Override
    public List<LotteryDTO> getAllUsersInLottery(String date, String location){
        return jdbcTemplate.query(
                "select * from lottery where date=? and location=? order by user_id",
                new LotteryMapperUserId(),
                new Object[]{date, location});
    }

    @Override
    public List<DeskDTO> getAllDeskIdInLocation(String location){
        return jdbcTemplate.query(
                "select desk_id from Desk where desk_location=?",
                new DeskIdMapper(),
                new Object[]{location});
    }

    @Override
    public void resolveLottery(String date, String location){
        jdbcTemplate.update(
                "update lottery set resolved=true where date=? and location=?",
                new Object[]{date, location});
    }

    @Override
    public List<UserDTO> getAllUsers(){
        return jdbcTemplate.query(
                "select * from User",
                new UserInfoMapper());
    }

    @Override
    public List<String> getAllLocations(){
        return jdbcTemplate.query(
                "select distinct desk_location from Desk",
                new LocationMapper());
    }

    @Override
    public BookingDTO getNextUserBooking(int user_id){
        return (BookingDTO) jdbcTemplate.queryForObject(
                "select booking_date, desk_number, desk_location FROM Booking JOIN Desk ON Booking.Desk_desk_id = Desk.desk_id where User_user_id=? and booking_date >= CURDATE() ORDER BY Booking_date LIMIT 1",
                new BookingMapper(),
                new Object[]{user_id});
    }

    @Override
    public List<BookingDTO> getAllBookingFromDateAndLocation(String date, String location){
        return jdbcTemplate.query(
                "SELECT booking_date, desk_number, desk_location, first_name, last_name FROM booking JOIN desk ON booking.Desk_desk_id = Desk.desk_id JOIN User ON booking.User_user_id=User.user_id where Booking.booking_date=? and Desk.desk_location=?",
                new AllBookingsMapper(),
                new Object[]{date, location});
    }

    @Override
    public boolean addDesks(int numDesks, String deskLocation){
        try{
            for(int i = 1 ; i <= numDesks ; i++){
                jdbcTemplate.update(
                        "insert into Desk(desk_number, desk_location) values(?,?)",
                        new Object[]{i, deskLocation});
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

}
