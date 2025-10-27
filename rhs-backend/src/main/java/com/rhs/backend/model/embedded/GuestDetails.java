package com.rhs.backend.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestDetails {

    @Field("guest_name")
    private String guestName;

    @Field("guest_id_number")
    private String guestIdNumber;

    @Field("guest_phone")
    private String guestPhone;

    @Field("id_document")
    private String idDocument;

    // Builder pattern
    public static GuestDetailsBuilder builder() {
        return new GuestDetailsBuilder();
    }

    public static class GuestDetailsBuilder {
        private String guestName;
        private String guestIdNumber;
        private String guestPhone;
        private String idDocument;

        public GuestDetailsBuilder guestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public GuestDetailsBuilder guestIdNumber(String guestIdNumber) {
            this.guestIdNumber = guestIdNumber;
            return this;
        }

        public GuestDetailsBuilder guestPhone(String guestPhone) {
            this.guestPhone = guestPhone;
            return this;
        }

        public GuestDetailsBuilder idDocument(String idDocument) {
            this.idDocument = idDocument;
            return this;
        }

        public GuestDetails build() {
            GuestDetails details = new GuestDetails();
            details.guestName = this.guestName;
            details.guestIdNumber = this.guestIdNumber;
            details.guestPhone = this.guestPhone;
            details.idDocument = this.idDocument;
            return details;
        }
    }
}