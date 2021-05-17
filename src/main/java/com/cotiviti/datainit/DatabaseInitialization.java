package com.cotiviti.datainit;

import com.cotiviti.constants.StatusConstant;
import com.cotiviti.datainit.config.CustomerInitConfiguration;
import com.cotiviti.datainit.config.GroupInitConfiguration;
import com.cotiviti.datainit.config.RoleGroupMapInitConfiguration;
import com.cotiviti.datainit.config.RoleInitConfiguration;
import com.cotiviti.datainit.config.StatusInitConfiguration;
import com.cotiviti.dto.RoleGroupMapYml;
import com.cotiviti.entities.Customer;
import com.cotiviti.entities.Role;
import com.cotiviti.entities.RoleGroupMap;
import com.cotiviti.entities.Status;
import com.cotiviti.entities.UserGroup;
import com.cotiviti.repository.CustomerRepository;
import com.cotiviti.repository.RoleGroupMapRepository;
import com.cotiviti.repository.RoleRepository;
import com.cotiviti.repository.StatusRepository;
import com.cotiviti.repository.UserGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Component
@Scope("singleton")
public class DatabaseInitialization {

    @Autowired
    private StatusInitConfiguration statusInitConfiguration;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoleInitConfiguration roleInitConfiguration;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupInitConfiguration groupInitConfiguration;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private RoleGroupMapInitConfiguration roleGroupMapInitConfiguration;

    @Autowired
    private RoleGroupMapRepository roleGroupMapRepository;

    @Autowired
    private CustomerInitConfiguration customerInitConfiguration;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void init() {
        try {
            log.info("Database Initialization.");
            populateStatus();
            populateRoles();
            populateGroups();
            populateGroupRoleMap();
            populateCustomer();
        } catch (Exception e) {
            log.error("Error In Database Initialization");
            log.error("Exception :: " + e.getMessage());
        }
    }

    private void populateStatus() {
        List<Status> allStatus = statusInitConfiguration.getStatusList();
        if (statusRepository.count() <= 0) {
            statusRepository.saveAll(allStatus);
        } else {
            List<String> statusNameList = allStatus.stream().map(Status::getName).collect(Collectors.toList());
            List<String> persistedStatus = statusRepository.getAllStatusByNames(statusNameList);
            List<Status> statusToPersist = allStatus.stream().filter(status -> !persistedStatus.contains(status.getName())).collect(Collectors.toList());
            if (!statusToPersist.isEmpty()) {
                statusRepository.saveAll(statusToPersist);
            }
        }
        log.info("Status Populated.");
    }

    private void populateRoles() {
        List<Role> allRoles = roleInitConfiguration.getRoleList();
        if (roleRepository.count() <= 0) {
            roleRepository.saveAll(allRoles);
        } else {
            List<String> roleNameList = allRoles.stream().map(Role::getName).collect(Collectors.toList());
            List<String> persistedRoleName = roleRepository.getAllRoleByNames(roleNameList);
            List<Role> rolesToPersist = allRoles.stream().filter(role -> !persistedRoleName.contains(role.getName())).collect(Collectors.toList());
            if (!rolesToPersist.isEmpty()) {
                roleRepository.saveAll(rolesToPersist);
            }
        }
        log.info("Role Populated.");
    }

    private void populateGroups() {
        List<UserGroup> allGroups = groupInitConfiguration.getGroupList();
        if (userGroupRepository.count() <= 0) {
            userGroupRepository.saveAll(allGroups);
        } else {
            List<String> groupNameList = allGroups.stream().map(UserGroup::getName).collect(Collectors.toList());
            List<String> persistedGroupName = userGroupRepository.getAllUserGroupByNames(groupNameList);
            List<UserGroup> groupToPersist = allGroups.stream().filter(group -> !persistedGroupName.contains(group.getName())).collect(Collectors.toList());
            if (!groupToPersist.isEmpty()) {
                userGroupRepository.saveAll(groupToPersist);
            }
        }
        log.info("Group Populated.");
    }

    private void populateGroupRoleMap() {
        List<RoleGroupMapYml> allRoleGroups = roleGroupMapInitConfiguration.getRoleGroupMapList();
        if (roleGroupMapRepository.count() <= 0) {
            List<RoleGroupMap> roleGroupMapList = allRoleGroups.stream()
                    .map(roleGroupMapYml -> {
                        return setRoleGroupMap(roleGroupMapYml);
                    }).collect(Collectors.toList());
            roleGroupMapRepository.saveAll(roleGroupMapList);
        } else {
            List<RoleGroupMap> allRoleGroupMap = roleGroupMapRepository.findAll();
            List<RoleGroupMap> mapsToPersist = new ArrayList<>();
            allRoleGroups.forEach(roleGroupMapYml -> {
                int count = 0;
                for (RoleGroupMap roleGroupMap : allRoleGroupMap) {
                    if (roleGroupMapYml.getGroupName().equalsIgnoreCase(roleGroupMap.getUserGroup().getName())
                            && roleGroupMapYml.getRoleName().equalsIgnoreCase(roleGroupMap.getRole().getName())) {
                        count++;
                        break;
                    }
                }
                if (count == 0) {
                    mapsToPersist.add(setRoleGroupMap(roleGroupMapYml));
                }
            });
            if (!mapsToPersist.isEmpty()) {
                roleGroupMapRepository.saveAll(mapsToPersist);
            }
        }
        log.info("RoleGroupMap Populated.");
    }

    private RoleGroupMap setRoleGroupMap(RoleGroupMapYml roleGroupMapYml) {
        RoleGroupMap map = new RoleGroupMap();
        map.setRole(roleRepository.getByName(roleGroupMapYml.getRoleName()));
        map.setUserGroup(userGroupRepository.getByName(roleGroupMapYml.getGroupName()));
        map.setIsActive(true);
        return map;
    }

    private void populateCustomer() {
        List<Customer> initCustomers = customerInitConfiguration.getCustomerList();
        Status activeStatus = statusRepository.getByName(StatusConstant.CREATE_APPROVE.getName());
        UserGroup userGroup = userGroupRepository.getByName("Customer");
        List<Customer> userToPersist = new ArrayList<>();
        if (customerRepository.count() <= 0) {
            userToPersist = initCustomers.stream().map(customer -> setCustomer(customer, activeStatus, userGroup)).collect(Collectors.toList());
            customerRepository.saveAll(userToPersist);
        } else {
            List<String> userNameList = initCustomers.stream().map(Customer::getUsername).collect(Collectors.toList());
            List<String> persistedUsers = customerRepository.getAllUsernames(userNameList);
            userToPersist = initCustomers.stream()
                    .filter(customer -> !persistedUsers.contains(customer.getUsername()))
                    .map(customer -> setCustomer(customer, activeStatus, userGroup)).collect(Collectors.toList());
            if (!userToPersist.isEmpty()) {
                customerRepository.saveAll(userToPersist);
            }
        }
        log.info("Customer Populated.");
    }

    private Customer setCustomer(Customer customer, Status activeStatus, UserGroup userGroup) {
        customer.setWrongPasswordAttemptCount(0);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setStatus(activeStatus);
        customer.setUserGroup(userGroup);
        customer.setLoginExpired(true);
        return customer;
    }

}
