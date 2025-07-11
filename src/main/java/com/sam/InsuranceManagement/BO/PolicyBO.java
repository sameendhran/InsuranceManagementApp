package com.sam.InsuranceManagement.BO;

import com.sam.InsuranceManagement.DAO.CoverageRepository;
import com.sam.InsuranceManagement.DAO.CustomerRepository;
import com.sam.InsuranceManagement.DAO.PolicyRepository;
import com.sam.InsuranceManagement.DAO.PolicyTypeRepository;
import com.sam.InsuranceManagement.DTO.policy.PolicyRequestDTO;
import com.sam.InsuranceManagement.DTO.policy.PolicyResponseDTO;
import com.sam.InsuranceManagement.Entity.Coverage;
import com.sam.InsuranceManagement.Entity.Customer;
import com.sam.InsuranceManagement.Entity.Policy;
import com.sam.InsuranceManagement.Entity.PolicyType;
import com.sam.InsuranceManagement.Exception.PolicyException;
import com.sam.InsuranceManagement.Projection.PolicyBasicInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PolicyBO {

    private final PolicyRepository repository;
    private final CustomerRepository customerRepository;
    private final CoverageRepository coverageRepository;
    private final PolicyTypeRepository policyTypeRepository;

    // === Add ===
    public PolicyResponseDTO addPolicy(PolicyRequestDTO dto) throws PolicyException {
        Policy policy = mapRequestToEntity(dto);           //  map DTO to entity
        validatePolicy(policy);                           //  validation
        policy = repository.save(policy);                 //  save policy
        return mapEntityToResponse(policy);               //  return DTO
    }

    // === GetById ===
    public PolicyResponseDTO getPolicyById(int id) throws PolicyException {
        Policy policy = repository.findById(id)
                .orElseThrow(() -> new PolicyException("Policy not found with ID: " + id));
        return mapEntityToResponse(policy);               //  map to DTO
    }

    // === GetAll ===
    public List<PolicyResponseDTO> getAllPolicies() throws PolicyException {
        List<Policy> policies = repository.findAll();
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found.");
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    // === Get By CustomerId ===
    public List<PolicyResponseDTO> getPoliciesByCustomerId(int customerId) throws PolicyException {
        List<Policy> policies = repository.findByCustomerCustomerId(customerId);
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found for Customer ID: " + customerId);
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    // ===== JPQL Queries ======

    // Fetch policies with premium above the given amount
    public List<PolicyResponseDTO> getPoliciesWithPremiumGreaterThan(double premium) throws PolicyException {
        List<Policy> policies = repository.findByPremiumGreaterThan(premium);
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found with premium greater than " + premium);
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    // Get all policies created between two dates
    public List<PolicyResponseDTO> getPoliciesByCreatedDateRange(Date startDate, Date endDate) throws PolicyException {
        List<Policy> policies = repository.findPoliciesByCreatedDateBetween(startDate, endDate);
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found between given dates.");
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    // === Get Policies by Customer First Name ===
    public List<PolicyResponseDTO> getPoliciesByCustomerName(String name) throws PolicyException {
        List<Policy> policies = repository.findByCustomerFirstNameIgnoreCase(name);
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found for customer name: " + name);
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    // === Get Policies By Customer Gender ===
    public List<PolicyResponseDTO> getPoliciesByCustomerGender(char gender) throws PolicyException {
        List<Policy> policies = repository.findPoliciesByCustomerGender(gender);   // fetch policies
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found for customers with gender: " + gender);  // if none found
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());   // map to DTOs
    }

    // === Get Policies by Customer City ===
    public List<PolicyResponseDTO> getPoliciesByCustomerCity(String cityName) throws PolicyException {
        List<Policy> policies = repository.findByCustomerCityNameIgnoreCase(cityName);
        if (policies.isEmpty()) {
            throw new PolicyException("No policies found for city: " + cityName);
        }
        return policies.stream().map(this::mapEntityToResponse).collect(Collectors.toList());
    }



    // === Validation ===
    private void validatePolicy(Policy policy) throws PolicyException {
        if (policy.getPolicyNumber() == null || policy.getPolicyNumber().isEmpty()) {
            throw new PolicyException("Policy number cannot be blank.");
        }
        if (policy.getPremium() <= 0) {
            throw new PolicyException("Premium must be greater than zero.");
        }
        if (policy.getCoverage() == null) throw new PolicyException("Coverage is required.");
        if (policy.getPolicyType() == null) throw new PolicyException("Policy Type is required.");
        if (policy.getCustomer() == null) throw new PolicyException("Customer is required.");
    }

    // === Mapper: Request -> Entity ===
    private Policy mapRequestToEntity(PolicyRequestDTO dto) throws PolicyException {
        Coverage coverage = coverageRepository.findById(dto.getCoverageId())
                .orElseThrow(() -> new PolicyException("Coverage not found with ID: " + dto.getCoverageId()));
        PolicyType policyType = policyTypeRepository.findById(dto.getPolicyTypeId())
                .orElseThrow(() -> new PolicyException("Policy Type not found with ID: " + dto.getPolicyTypeId()));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new PolicyException("Customer not found with ID: " + dto.getCustomerId()));

        Policy policy = new Policy();
        policy.setPolicyNumber(dto.getPolicyNumber());
        policy.setPremium(dto.getPremium());
        policy.setCoverage(coverage);
        policy.setPolicyType(policyType);
        policy.setCustomer(customer);
        return policy;
    }

    // === Mapper: Entity -> Response ===
    private PolicyResponseDTO mapEntityToResponse(Policy policy) {
        PolicyResponseDTO dto = new PolicyResponseDTO();
        dto.setPolicyId(policy.getPolicyId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setPremium(policy.getPremium());
        dto.setCoverageId(policy.getCoverage().getCoverageId());
        dto.setPolicyTypeId(policy.getPolicyType().getTypeId());
        dto.setCustomerId(policy.getCustomer().getCustomerId());
        dto.setCreatedDate(
                policy.getCreatedDate() != null
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(policy.getCreatedDate())
                        : null
        );
        dto.setUpdatedDate(
                policy.getUpdatedDate() != null
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(policy.getUpdatedDate())
                        : null
        );


        return dto;
    }

    // ==== projection =====

    // Add method to fetch projection
    public List<PolicyBasicInfo> fetchBasicPolicyInfo() throws PolicyException {
        List<PolicyBasicInfo> list = repository.fetchBasicPolicyInfo();
        if (list.isEmpty()) {
            throw new PolicyException("No basic policy info found.");
        }
        return list;
    }

    // Aggregate function ====

    // Calls repository method to get total policy count
    public Long countPolicies() {
        return repository.countTotalPolicies();
    }

    // Calls repository method to get total premium sum
    public Double getTotalPremium() {
        return repository.sumAllPremiums();
    }

    // Calls repository to get policy count per city
    public List<Object[]> getPolicyCountByCity() {
        return repository.countPoliciesByCity();
    }


}
