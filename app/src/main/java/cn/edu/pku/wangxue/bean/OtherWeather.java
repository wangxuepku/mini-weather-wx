package cn.edu.pku.wangxue.bean;

/**
 * Created by wx on 2016/12/6.
 */
public class OtherWeather {
    private String date;
    private String high;
    private String low;
    private String type;
    private String fengxiang;

    public OtherWeather() {
        fengxiang = "";
        date = "";
        high = "";
        low = "";
        type = "";
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }
}
