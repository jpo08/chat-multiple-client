package serverPac;

public class Address {
    public String ip;
    public int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Address(String ip, int port){

        this.ip = ip;

        this.port  = port;
    
    }
}
