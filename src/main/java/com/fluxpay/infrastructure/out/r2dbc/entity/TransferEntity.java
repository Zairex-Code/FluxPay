package com.fluxpay.infrastructure.out.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("transfers") 
public class TransferEntity implements Persistable<String> {

    @Id
    private String id;
    private String originAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    // Este campo no se guardará en la base de datos gracias a @Transient.
    // Le indicamos a Spring que, por defecto, todo objeto construido en memoria es nuevo.
    @Transient
    @Builder.Default
    private boolean isNewRecord = true;

    // Método de la interfaz Persistable. Spring lo llamará para decidir entre INSERT o UPDATE.
    @Override
    @Transient
    public boolean isNew() {
        return this.isNewRecord;
    }
}