package com.clientnest.business;

import com.clientnest.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private UUID businessId;

    @NotBlank
    @Size(max = 255)
    private String businessName;

    @Size(max = 1000)
    private String businessDescription;

    @NotBlank
    @Size(max = 255)
    @Email
    @Column(unique = true)
    private String businessEmail;

    @NotBlank
    @Size(max = 100)
    private String businessAddress;

    @NotBlank
    @Size(max = 50)
    private String businessCity;

    @NotBlank
    @Size(max = 50)
    private String businessState;

    @NotBlank
    @Size(max = 50)
    private String businessPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_owner_id", nullable = false)
    private User businessOwner;

    @NotNull
    private String businessUrl;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @NotNull
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Business business = (Business) o;
        return Objects.equals(businessId, business.businessId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(businessId);
    }

    @Override
    public String toString() {
        return "Business{" +
                "businessId=" + businessId +
                ", businessName='" + businessName + '\'' +
                ", businessDescription='" + businessDescription + '\'' +
                ", businessEmail='" + businessEmail + '\'' +
                ", businessAddress='" + businessAddress + '\'' +
                ", businessCity='" + businessCity + '\'' +
                ", businessState='" + businessState + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", businessOwner=" + businessOwner +
                ", businessUrl='" + businessUrl + '\'' +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
