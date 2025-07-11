package com.sam.InsuranceManagement.DAO;

import com.sam.InsuranceManagement.Entity.Policy;
import com.sam.InsuranceManagement.Projection.PolicyBasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {

    List<Policy> findByCustomerCustomerId(int customerId);

    // Get all policies with premium greater than the given value
    List<Policy> findByPremiumGreaterThan(double premium);

    // Get policies created between two dates
    @Query("SELECT p FROM Policy p WHERE p.createdDate BETWEEN :startDate AND :endDate")
    List<Policy> findPoliciesByCreatedDateBetween(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // Get policies by customer first name (case-insensitive)
    @Query("SELECT p FROM Policy p WHERE LOWER(p.customer.firstName) = LOWER(:firstName)")
    List<Policy> findByCustomerFirstNameIgnoreCase(@Param("firstName") String firstName);

    // Get all policies where customer's gender matches
    @Query("SELECT p FROM Policy p WHERE p.customer.gender = :gender")
    List<Policy> findPoliciesByCustomerGender(@Param("gender") char gender);

    // Get all policies where the customer's city name matches (case-insensitive)
    @Query("SELECT p FROM Policy p WHERE LOWER(p.customer.city.cityName) = LOWER(:cityName)")
    List<Policy> findByCustomerCityNameIgnoreCase(@Param("cityName") String cityName);

    // Custom projection: fetch only few columns (policyNumber, premium, customerId)
    @Query("SELECT p.policyNumber AS policyNumber, p.premium AS premium, p.customer.customerId AS customerId FROM Policy p")
    List<PolicyBasicInfo> fetchBasicPolicyInfo();


    // Count total number of policies using JPQL aggregate function
    @Query("SELECT COUNT(p) FROM Policy p")
    Long countTotalPolicies();


    // Calculate the sum of all policy premiums
    @Query("SELECT SUM(p.premium) FROM Policy p")
    Double sumAllPremiums();

    // Group policies by city and count how many policies each city has
    @Query("SELECT p.customer.city.cityName AS city, COUNT(p) AS total FROM Policy p GROUP BY p.customer.city.cityName")
    List<Object[]> countPoliciesByCity();

}
