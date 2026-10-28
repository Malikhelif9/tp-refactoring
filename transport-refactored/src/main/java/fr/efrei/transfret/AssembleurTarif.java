package fr.efrei.transfret;

import java.util.Map;
import java.util.Objects;

public class AssembleurTarif {

    private final Map<ModeTransport, TarifTransport> tarifsParMode;
    private final Map<TypeClient, PolitiqueRemise> remisesParTypeClient;
    private final boolean supplementHorsGabarit;

    public AssembleurTarif(Map<ModeTransport, TarifTransport> tarifsParMode,
                           Map<TypeClient, PolitiqueRemise> remisesParTypeClient) {
        this(tarifsParMode, remisesParTypeClient, false);
    }

    public AssembleurTarif(Map<ModeTransport, TarifTransport> tarifsParMode,
                           Map<TypeClient, PolitiqueRemise> remisesParTypeClient,
                           boolean supplementHorsGabarit) {
        this.tarifsParMode = Map.copyOf(tarifsParMode);
        this.remisesParTypeClient = Map.copyOf(remisesParTypeClient);
        this.supplementHorsGabarit = supplementHorsGabarit;
        for (ModeTransport mode : ModeTransport.values()) {
            Objects.requireNonNull(this.tarifsParMode.get(mode), "Tarif manquant pour " + mode);
        }
        for (TypeClient typeClient : TypeClient.values()) {
            Objects.requireNonNull(this.remisesParTypeClient.get(typeClient), "Remise manquante pour " + typeClient);
        }
    }

    public TarifTransport assembler(ModeTransport mode) {
        TarifTransport tarif = Objects.requireNonNull(tarifsParMode.get(mode), "Mode inconnu : " + mode);
        tarif = new SupplementFragile(tarif);
        tarif = new SupplementDangereux(tarif);
        tarif = new SupplementRefrigere(tarif);
        if (supplementHorsGabarit) {
            tarif = new SupplementHorsGabarit(tarif);
        }
        tarif = new SupplementUrgence(tarif);
        tarif = new RemiseClient(tarif, remisesParTypeClient);
        return new Assurance(tarif);
    }
}
