package org.project.ecommerce.user.dto;

import lombok.Data;
import org.project.ecommerce.user.enums.UserRole;

@Data
public class UpgradeRoleRequest {
    String username;
    UserRole userRole;
}
