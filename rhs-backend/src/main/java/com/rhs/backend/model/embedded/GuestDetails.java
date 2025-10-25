// ========== GuestDetails.java ==========
package com.rhs.backend.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestDetails {
    @Field("guest_name")
    private String guestName;

    @Field("guest_surname")
    private String guestSurname;

    @Field("guest_id_number")
    private String guestIdNumber;

    @Field("contact_number")
    private String contactNumber;

    @Field("id_document_url")
    private String idDocumentUrl;
}
