package serverPac;

public interface Services {

    public void subscribe(String user, String ip, int port);

    public void createGroups(String groupName, String ip);

    public void forwardTextMessageToGroup(String groupName, String message);

    public void forwardTextMessage(String m, String user);

    public void forwardVoiceMessage(String user);

    public void checkAudiosFromUser(String user);

    public void forwardVoiceCall();


}
