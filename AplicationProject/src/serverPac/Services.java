package serverPac;

public interface Services {

    public void subscribe(String user, String ip, int port);

    public void createGroups();

    public void forwardTextMessage(String m, String user);

    public void forwardVoiceMessage();

    public void forwardVoiceCall();


}
