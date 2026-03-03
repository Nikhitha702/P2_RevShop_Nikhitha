package com.revshop.mapper;

import com.revshop.dto.BuyerRegisterRequest;
import com.revshop.dto.SellerRegisterRequest;
import com.revshop.entity.Role;
import com.revshop.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toBuyer(BuyerRegisterRequest request, String encodedPassword) {
        User user = mapCommonFields(request, encodedPassword);
        user.setRole(Role.ROLE_BUYER);
        return user;
    }

    public static User toSeller(SellerRegisterRequest request, String encodedPassword) {
        User user = mapCommonFields(request, encodedPassword);
        user.setRole(Role.ROLE_SELLER);
        user.setBusinessName(request.getBusinessName().trim());
        user.setGstNumber(request.getGstNumber().trim());
        user.setBusinessCategory(request.getBusinessCategory().trim());
        return user;
    }

    private static User mapCommonFields(BuyerRegisterRequest request, String encodedPassword) {
        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(encodedPassword);
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress().trim());
        user.setEnabled(true);
        return user;
    }
}
