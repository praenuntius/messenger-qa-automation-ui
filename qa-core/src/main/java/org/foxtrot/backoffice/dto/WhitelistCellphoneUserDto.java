package org.foxtrot.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhitelistCellphoneUserDto {

    private long organizationId;
    private long phone;
    private String responsible;
    private String initiator;
}
