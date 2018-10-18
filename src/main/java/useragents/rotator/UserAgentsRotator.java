package useragents.rotator;

public class UserAgentsRotator {

    /*
     * Only one instance of UserAgentsRotator is to be present
     * It rotates the UserAgent String everytime when a crawl
     * request has been submitted.
     */

    private static UserAgentsRotator rotator = new UserAgentsRotator();

    //Making Constructor Private to avoid initializing the object.
    private UserAgentsRotator(){}

    //Maitains the previous state of UserAgent - Volatile as it makes it threadsafe - though a singleton - Writes go to main memory
    private volatile short previousState;

    public static UserAgentsRotator singletonRotator(){
        return rotator;
    }

    public void setPreviousState(short previousState){this.previousState = previousState;}

    private short getPreviousState(){return previousState;}

    public String userAgentRotator(short randomvar){
        //Avoiding Collections as array takes less space and faster for get and put operations.
        String[] arr = new String[20];
        arr[0] = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        arr[1] = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";
        arr[2] = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36";
        arr[3] = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36";
        arr[4] = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36";
        arr[5] = "Mozilla/5.0 (Windows NT 6.0; rv:2.0) Gecko/20100101 Firefox/4.0 Opera 12.14";
        arr[6] = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36";
        arr[7] = "Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02";
        arr[8] = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";
        arr[9] = "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0";
        arr[10] = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";
        arr[11] = "Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0";
        arr[12] = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20130401 Firefox/31.0";
        arr[13] = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
        arr[14] = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20120101 Firefox/29.0";
        arr[15] = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/29.0";
        arr[16] = "Mozilla/5.0 (X11; OpenBSD amd64; rv:28.0) Gecko/20100101 Firefox/28.0";
        arr[17] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246";
        arr[18] = "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16";
        arr[19] = "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14";

        //Makes sure we don't submit the same UserAgent again and again
        if(randomvar != this.getPreviousState()){
            return arr[randomvar];
        }else{
            if(randomvar == 0){
                return arr[this.getPreviousState() + 1];
            }
            if(randomvar == 19){
                return arr[this.getPreviousState() - 1];
            }
        }

        return (randomvar > 19 || randomvar < 0) ? arr[4] : arr[randomvar];
    }

}
