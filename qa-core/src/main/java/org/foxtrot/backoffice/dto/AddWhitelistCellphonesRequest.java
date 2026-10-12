package org.foxtrot.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddWhitelistCellphonesRequest {

    private List<WhitelistCellphoneUserDto> users;
}
