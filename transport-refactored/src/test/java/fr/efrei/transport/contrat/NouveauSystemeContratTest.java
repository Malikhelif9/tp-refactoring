package fr.efrei.transport.contrat;

import fr.efrei.transfret.NouveauSystemeAdapter;

class NouveauSystemeContratTest extends ContratTransportTest {

    @Override
    protected SystemeTransport creerSysteme() {
        return new NouveauSystemeAdapter();
    }
}
