package works.luii.timbangan;

public interface Notify {
    void connectionSuccessful();
    void messageIncomming(String message);
    void dataReceiveDone(float datakg);
}
