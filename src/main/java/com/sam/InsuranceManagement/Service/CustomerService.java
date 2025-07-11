package com.sam.InsuranceManagement.Service;

import com.sam.InsuranceManagement.BO.CustomerBO;
import com.sam.InsuranceManagement.DTO.customer.CustomerRequestDTO;
import com.sam.InsuranceManagement.DTO.customer.CustomerResponseDTO; // 💡 CHANGED: new import
import com.sam.InsuranceManagement.Entity.Customer;
import com.sam.InsuranceManagement.Exception.CustomerException;
import com.sam.InsuranceManagement.Response.ResponseObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; //  transactional

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private static final String ERROR_PREFIX = "Error: ";
    private static final String SUCCESS_FETCH = "Customers fetched successfully";

    private final CustomerBO bo;

    public ResponseObject addCustomer(CustomerRequestDTO dto) {
        ResponseObject response = new ResponseObject();
        try {
            Customer customer = bo.addCustomer(dto);
            response.setSuccessMessage("Customer added successfully");
            response.setCustomerDTO(mapToDTO(customer)); // return DTO
        } catch (CustomerException e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in addCustomer", e);
        }
        return response;
    }

    public ResponseObject updateCustomer(int id, CustomerRequestDTO dto) {
        ResponseObject response = new ResponseObject();
        try {
            Customer updatedCustomer = bo.updateCustomer(id, dto);
            response.setSuccessMessage("Customer updated successfully");
            response.setCustomerDTO(mapToDTO(updatedCustomer)); // return DTO
        } catch (CustomerException e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in updateCustomer", e);
        }
        return response;
    }

    public ResponseObject getAllCustomers() {
        ResponseObject response = new ResponseObject();
        try {
            List<Customer> customers = bo.getAllCustomers();
            response.setCustomerListDTO(customers.stream().map(this::mapToDTO).toList()); //  return list of DTOs
        } catch (CustomerException e) {
            response.setFailureMessage("Error fetching customers: " + e.getMessage());
            log.error("Error in getAllCustomers", e);
        }
        return response;
    }

    @Transactional //  Added transactional
    public ResponseObject getCustomerById(int id) {
        ResponseObject response = new ResponseObject();
        try {
            Customer customer = bo.getCustomerById(id);
            response.setCustomerDTO(mapToDTO(customer)); //  return DTO
        } catch (CustomerException e) {
            response.setFailureMessage("Error fetching customer: " + e.getMessage());
            log.error("Error in getCustomerById", e);
        }
        return response;
    }

    // ===== JPQL Quries =====      ===== JPOL Quries ======

    public ResponseObject getCustomersByCityName(String cityName) {
        ResponseObject response = new ResponseObject();
        try {
            // Fetch customers using the BO layer
            List<Customer> customers = bo.getCustomersByCityName(cityName);
            response.setCustomerListDTO(
                    customers.stream().map(this::mapToDTO).toList()
            );
            response.setSuccessMessage(SUCCESS_FETCH);
        } catch (CustomerException e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in getCustomersByCityName", e);
        }
        return response;
    }

    public ResponseObject getCustomersByGender(char gender) {
        ResponseObject response = new ResponseObject();
        try {
            List<Customer> customers = bo.getCustomersByGender(gender);
            response.setCustomerListDTO(
                    customers.stream().map(this::mapToDTO).toList()
            );
            response.setSuccessMessage(SUCCESS_FETCH);
        } catch (CustomerException e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in getCustomersByGender", e);
        }
        return response;
    }

    public ResponseObject getCustomersBornBefore(LocalDate date) {
        ResponseObject response = new ResponseObject();
        try {
            // Convert LocalDate to java.util.Date
            Date dobDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Customer> customers = bo.getCustomersBornBefore(dobDate);

            response.setCustomerListDTO(
                    customers.stream().map(this::mapToDTO).toList()
            );
            response.setSuccessMessage(SUCCESS_FETCH);
        } catch (Exception e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in getCustomersBornBefore", e);
        }
        return response;
    }

    public ResponseObject getCustomerCountByCity(String cityName) {
        ResponseObject response = new ResponseObject();
        try {
            long count = bo.getCustomerCountByCityName(cityName);
            response.setCustomerCount(count);
            response.setSuccessMessage("Count fetched successfully");
        } catch (CustomerException e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in getCustomerCountByCity", e);
        }
        return response;
    }

    public ResponseObject getCustomersWithPremiumAbove(double amount) {
        ResponseObject response = new ResponseObject();
        try {
            List<Customer> customers = bo.getCustomersWithPremiumAbove(amount);
            response.setCustomerListDTO(
                    customers.stream().map(this::mapToDTO).toList()
            );
            response.setSuccessMessage(SUCCESS_FETCH);
        } catch (CustomerException e) {
            response.setFailureMessage(ERROR_PREFIX + e.getMessage());
            log.error("Error in getCustomersWithPremiumAbove", e);
        }
        return response;
    }

    public ResponseObject getCustomersByPolicyType(String typeName) {
        ResponseObject response = new ResponseObject();
        try {
            List<Customer> customers = bo.getCustomersByPolicyType(typeName);
            response.setCustomerListDTO(
                    customers.stream().map(this::mapToDTO).toList()
            );
            response.setSuccessMessage("Found customers with policy type " + typeName);
        } catch (CustomerException e) {
            response.setFailureMessage(e.getMessage());
        }
        return response;
    }

    public ResponseObject getCustomersByOccupationId(int occupationId) {
        ResponseObject response = new ResponseObject();
        try {
            List<Customer> customers = bo.getCustomersByOccupationId(occupationId);
            response.setCustomerListDTO(customers.stream().map(this::mapToDTO).toList());
            response.setSuccessMessage("Customers fetched successfully by occupation ID");
        } catch (CustomerException e) {
            response.setFailureMessage("Error fetching customers: " + e.getMessage());
        }
        return response;
    }

    /**
     * @Transactional is needed here because this method loads Customer entities
     * that have lazy-loaded relationships (e.g. city or policies). Keeping the
     * transaction open until the method finishes ensures those lazy fields can
     * still be fetched when mapping Customers to DTOs, preventing LazyInitializationException.
     */

    @Transactional
    public ResponseObject getCustomersBornAfter(LocalDate date) {
        ResponseObject response = new ResponseObject();
        try {
            Date dateAsDate = java.sql.Date.valueOf(date); // Convert LocalDate to SQL Date
            List<Customer> customers = bo.getCustomersBornAfter(dateAsDate);
            response.setCustomerListDTO(customers.stream().map(this::mapToDTO).toList());
            response.setSuccessMessage("Customers who were born after " + date + " fetched successfully");
        } catch (CustomerException e) {
            response.setFailureMessage(e.getMessage());
        }
        return response;
    }

    @Transactional(readOnly = true)
    public ResponseObject getCustomersWithNoPolicies() {
        ResponseObject response = new ResponseObject();
        try {
            List<Customer> customers = bo.getCustomersWithNoPolicies();
            response.setCustomerListDTO(customers.stream().map(this::mapToDTO).toList());
            response.setSuccessMessage("Customers without policies fetched successfully");
        } catch (CustomerException e) {
            response.setFailureMessage(e.getMessage());
        }
        return response;
    }

    //  to map entity to DTO
    private CustomerResponseDTO mapToDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setGender(customer.getGender());
        dto.setDob(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(customer.getDob())); // ✅ Locale added
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setCityName(customer.getCity().getCityName());
        dto.setStateId(customer.getStateId());
        dto.setCountryId(customer.getCountryId());
        dto.setOccupationId(customer.getOccupationId());
        dto.setCreatedDate(
                customer.getCreatedDate() != null ? customer.getCreatedDate().toString() : null
        );
        dto.setUpdatedDate(
                customer.getUpdatedDate() != null ? customer.getUpdatedDate().toString() : null
        );
        return dto;
    }

}
