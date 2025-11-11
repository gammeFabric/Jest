package core.players;

public class VirtualPlayer extends Player {
    public VirtualPlayer(String name, boolean isVirtual) {
        super(name, isVirtual);
    }

    @Override
    public Offer makeOffer() {
        return null;
    }
}
