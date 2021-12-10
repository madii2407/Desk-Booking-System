package com.example.group11officedeskbooking.repository;

import com.example.group11officedeskbooking.DTO.Admin_BookingDTO;
import com.example.group11officedeskbooking.DTO.LotteryDTO;
import com.example.group11officedeskbooking.model.Admin_BookingMapper;
import com.example.group11officedeskbooking.model.LotteryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Admin_BookingRepositoryJDBC implements Admin_BookingRepository{


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public Admin_BookingRepositoryJDBC(JdbcTemplate aTemplate){
        jdbcTemplate = aTemplate;
    }

    @Override
    public List<Admin_BookingDTO> findAll() {
        return jdbcTemplate.query(
            "Select booking_id, booking_date, User_user_id, Desk_desk_id, User.first_name, User.last_name, desk.desk_location FROM booking join user ON booking.User_user_id = User.user_id join desk on booking.Desk_desk_id=Desk.desk_id",
                new Admin_BookingMapper());
    }

    @Override
    public List<LotteryDTO> getAllLotteryDays(){
        return jdbcTemplate.query(
                "select distinct date, location, resolved from lottery order by date",
                new LotteryMapper());
    }

    // Delete Bookings

    @Override
    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM Booking WHERE booking_id=?", id);
    }

}
