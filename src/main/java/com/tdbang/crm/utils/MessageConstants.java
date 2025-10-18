/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.utils;

public class MessageConstants {

    private MessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final int SUCCESS_STATUS = 1;
    public static final int ERROR_STATUS = 0;
    public static final String FETCHING_USER_INFO_SUCCESS = "Fetching user information successfully!";
    public static final String FETCHING_USER_INFO_ERROR = "Error while fetching user information";
    public static final String CREATING_NEW_USER_SUCCESS = "Create new user successfully!";
    public static final String CREATING_NEW_USER_ERROR = "Error while creating user";
    public static final String FETCHING_LIST_OF_USERS_SUCCESS = "Fetching list of users successfully!";
    public static final String FETCHING_LIST_OF_USERS_ERROR = "Error while fetching list of users";
    public static final String FETCHING_LIST_OF_NAMES_USERS_SUCCESS = "Fetching list of names of users successfully!";
    public static final String FETCHING_LIST_OF_NAMES_USERS_ERROR = "Error while fetching list of names of users";
    public static final String FETCHING_USER_BY_ID_SUCCESS = "Fetching user by ID successfully!";
    public static final String FETCHING_USER_BY_ID_ERROR = "Error while fetching user by ID";
    public static final String UPDATING_USER_SUCCESS = "Updating user information successfully!";
    public static final String UPDATING_USER_ERROR = "Error while updating user information";
    public static final String CHANGING_USER_PASSWORD_SUCCESS = "Changing user password successfully!";
    public static final String CHANGING_USER_PASSWORD_ERROR = "Error while changing user password";
    public static final String INCORRECT_OLD_PASSWORD = "Old Password is incorrect";
    public static final String CREATING_NEW_SALES_ORDER_SUCCESS = "Creating new sales order successfully!";
    public static final String CREATING_NEW_SALES_ORDER_ERROR = "Error while creating new sales order";
    public static final String FETCHING_LIST_OF_SALES_ORDER_SUCCESS = "Fetching list of sales order successfully!";
    public static final String FETCHING_LIST_OF_SALES_ORDER_ERROR = "Error while fetching list of sales order";
    public static final String FETCHING_SALES_ORDER_SUCCESS = "Fetching sales order successfully!";
    public static final String FETCHING_SALES_ORDER_ERROR = "Error while fetching sales order";
    public static final String UPDATING_SALES_ORDER_SUCCESS = "Updating sales order successfully!";
    public static final String UPDATING_SALES_ORDER_ERROR = "Error while updating sales order";
    public static final String DELETING_SALES_ORDER_SUCCESS = "Deleting sales order successfully!";
    public static final String DELETING_SALES_ORDER_ERROR = "Error while deleting sales order";
    public static final String DELETING_LIST_OF_SALES_ORDERS_SUCCESS = "Deleting list of sales orders successfully!";
    public static final String DELETING_LIST_OF_SALES_ORDERS_ERROR = "Error while deleting list of sales orders";
    public static final String FINDING_SALES_ORDER_SUCCESS = "Finding sales order successfully!";
    public static final String FINDING_SALES_ORDER_ERROR = "Error while finding sales order";
    public static final String COUNTING_NO_SALES_ORDERS_BY_STATUS_SUCCESS = "Counting number of sales order based on Status successfully!";
    public static final String COUNTING_NO_SALES_ORDERS_BY_STATUS_ERROR = "Error while counting number of sales order based on Status";
    public static final String CREATING_NEW_CONTACT_SUCCESS = "Creating new contact successfully!";
    public static final String CREATING_NEW_CONTACT_ERROR = "Error while creating new contact";
    public static final String FETCHING_LIST_OF_CONTACTS_SUCCESS = "Fetching list of contacts successfully!";
    public static final String FETCHING_LIST_OF_CONTACTS_ERROR = "Error while fetching list of contacts";
    public static final String FETCHING_LIST_OF_CONTACT_NAMES_SUCCESS = "Fetching list of contact names successfully!";
    public static final String FETCHING_LIST_OF_CONTACT_NAMES_ERROR = "Error while fetching list of contact names";
    public static final String FETCHING_CONTACT_SUCCESS = "Fetching contact successfully!";
    public static final String FETCHING_CONTACT_ERROR = "Error while fetching contact";
    public static final String UPDATING_CONTACT_SUCCESS = "Updating contact successfully!";
    public static final String DELETING_CONTACT_SUCCESS = "Deleting contact successfully!";
    public static final String DELETING_CONTACT_ERROR = "Error while deleting contact";
    public static final String DELETING_LIST_OF_CONTACTS_SUCCESS = "Deleting list of contacts successfully!";
    public static final String DELETING_LIST_OF_CONTACTS_ERROR = "Error while deleting list of contacts";
    public static final String FINDING_CONTACT_SUCCESS = "Finding contacts successfully!";
    public static final String FINDING_CONTACT_ERROR = "Error while finding contacts";
    public static final String COUNTING_NO_CONTACTS_BY_LEAD_SRC_SUCCESS = "Counting number of contacts based on Lead source successfully!";
    public static final String COUNTING_NO_CONTACTS_BY_LEAD_SRC_ERROR = "Error while counting number of contacts based on Lead source";
    public static final String FETCHING_LIST_OF_PRODUCTS_SUCCESS = "Fetching list of products successfully!";
    public static final String CREATING_NEW_PRODUCT_SUCCESS = "Creating new product successfully!";
    public static final String CREATING_NEW_PRODUCT_ERROR = "Error while creating new product";
    public static final String UPDATING_PRODUCT_SUCCESS = "Updating product successfully!";
    public static final String DELETING_LIST_OF_PRODUCTS_SUCCESS = "Deleting list of products successfully!";

    public static final String ERROR_CODE = "ERROR";
    public static final String SUCCESS_CODE = "SUCCESS";
    public static final String CREATED_CODE = "CREATED";
    public static final String NO_CONTENT_CODE = "NO_CONTENT";
    public static final String BAD_REQUEST_CODE = "BAD_REQUEST";
    public static final String VALIDATION_FAILED_CODE = "VALIDATION_FAILED";
    public static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    public static final String FORBIDDEN_CODE = "FORBIDDEN";
    public static final String NOT_FOUND_CODE = "NOT_FOUND";
    public static final String METHOD_NOT_ALLOWED_CODE = "METHOD_NOT_ALLOWED";
    public static final String CONFLICT_CODE = "CONFLICT";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";

    public static final String SUCCESS_MESSAGE = "Request processed successfully";
    public static final String CREATED_MESSAGE = "Resource created successfully";
    public static final String NO_CONTENT_MESSAGE = "Request processed, no content returned";
    public static final String BAD_REQUEST_MESSAGE = "Invalid request parameters";
    public static final String VALIDATION_FAILED_MESSAGE = "One or more fields failed validation";
    public static final String UNAUTHORIZED_MESSAGE = "Authentication required";
    public static final String FORBIDDEN_MESSAGE = "Access denied";
    public static final String NOT_FOUND_MESSAGE = "Requested resource not found";
    public static final String METHOD_NOT_ALLOWED_MESSAGE = "HTTP method not supported";
    public static final String CONFLICT_MESSAGE = "Resource already exists or version conflict";
    public static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred";
}
