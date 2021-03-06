import java.util.*;
import Model.Message;

/**
 * about almost function
 * - CLI 화면 출력
 * - 대부분의 기능 담당.
 */
public class Controller {
    final int MAX_SALESNUM = 7;
    public Controller() {
        myDVM = DVM.getInstance();
        myMessageManager = MessageManager.getInstance();
        paymentPage = new PaymentPage();
        prePaymentPage = new PrePaymentPage();
        verificationCodeMenu = new VerificationCodeMenu();

    }

    private String drinkCode;
    private int drinkCount;
    private DVM myDVM;
    private MessageManager myMessageManager;
    private ArrayList<Message> myMessage= new ArrayList<Message>();
    private String msgTeamID = "Team4";
    private PaymentPage paymentPage;
    private PrePaymentPage prePaymentPage;
    private VerificationCodeMenu verificationCodeMenu;
    private ArrayList<Message> receivedMsgList = new ArrayList<Message>();

    Scanner scan=new Scanner(System.in);

    public void showMenu() {
        int mode;

        while (true) {
            try {
                System.out.println("\n원하시는 메뉴의 번호를 입력해주세요.\n" +
                        "1. 음료 선택\n" +
                        "2. 인증코드 입력\n" +
                        "3. 관리자 모드");
                System.out.print(">");
                mode = scan.nextInt();
                break;
            } catch (InputMismatchException ime) {
                scan.next();
                System.out.println("잘못된 입력입니다. 정수만 입력해주세요.");
            }
        }

        while (!(mode > 0 && mode < 4)) {
            System.out.println("잘못된 입력입니다. 1, 2, 3 중에서만 입력해주세요.");
            System.out.println("원하시는 메뉴의 번호를 입력해주세요.\n" +
                    "1. 음료 선택\n" +
                    "2. 인증코드 입력\n" +
                    "3. 관리자 모드");
            System.out.print(">");
            while (true) {
                try {
                    mode = scan.nextInt();
                    break;
                } catch (InputMismatchException ime) {
                    scan.next();
                    System.out.println("잘못된 입력입니다. 정수만 입력해주세요.");
                    System.out.println("원하시는 메뉴의 번호를 입력해주세요.\n" +
                            "1. 음료 선택\n" +
                            "2. 인증코드 입력\n" +
                            "3. 관리자 모드");
                    System.out.print(">");
                }
            }
        }

        scan.nextLine();
        switch (mode) {
            case 1:
                showSelectItemPage();
                break;
            case 2:
                showVerificationCodeMenu();
                break;
            case 3:
                showAdminMenu();
                break;
        }
    }

    public int isValidCode() {
        switch(drinkCode){
            case "0":
                return 0;
            case "1":
                drinkCode = "01";
                return 1;
            case "2":
                drinkCode = "02";
                return 1;
            case "3":
                drinkCode = "03";
                return 1;
            case "4":
                drinkCode = "04";
                return 1;
            case "5":
                drinkCode = "05";
                return 1;
            case "6":
                drinkCode = "06";
                return 1;
            case "7":
                drinkCode = "07";
                return 1;
            case "8":
                drinkCode = "08";
                return 1;
            case "9":
                drinkCode = "09";
                return 1;
            case "01":
            case "02":
            case "03":
            case "04":
            case "05":
            case "06":
            case "07":
            case "08":
            case "09":
            case "10":
            case "11":
            case "12":
            case "13":
            case "14":
            case "15":
            case "16":
            case "17":
            case "18":
            case "19":
            case "20":
                return 1;
            default:
                return -1;
        }
    }

    public int isValidCount() {
        if(drinkCount == 0) {
            return 0;
        } else if((drinkCount > 0) && ( drinkCount < 1000)) {
            return 1;
        } else {
            return -1;
        }
    }

    public Location getClosestDVM() {
        Location returnLoc = new Location();
        Location myLoc = myDVM.getLocation();
        int myX = myLoc.getX();
        int myY = myLoc.getY();
        int minDistance = 9999;
        String minDvmID = " ";
        myMessageManager.sendReqMsg("StockCheckRequest", drinkCode, drinkCount);
        try{
            Thread.sleep(1000); //message가 도착하고 처리 되기까지 기다리기
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        while(receivedMsgList.size() > 0){
            Message temp = receivedMsgList.remove(0);
            int compareX = temp.getMsgDescription().getDvmXCoord();
            int compareY = temp.getMsgDescription().getDvmYCoord();
            String compareID = temp.getSrcId();
            //1. 거리를 구하고
            int resX = myX - compareX;
            if (resX < 0) resX *= -1;
            int resY = myY - compareY;
            if (resY < 0) resY *= -1;
            //2. min과 비교하고
            if( resX + resY < minDistance) {
            //3. 더 작다면 other DVM의 위치를 저장한다.
                minDvmID = compareID;
                msgTeamID = minDvmID;
                returnLoc.setX(compareX);
                returnLoc.setY(compareY);
            }else if (resX + resY == minDistance) {
                //같다면 id를 비교하고
                if(minDvmID.compareTo(compareID)>0){
                    minDvmID = compareID;
                    msgTeamID = minDvmID;
                    returnLoc.setX(compareX);
                    returnLoc.setY(compareY);
                };
            }
        }
        return returnLoc;
    }

    public boolean showOtherDVM(Location otherDVM) {
        if((otherDVM.getX() == 0) && (otherDVM.getY() == 0)){
            return false;
        }
        else{
            System.out.println("재고가 있는 가장 가까운 DVM의 위치는 " + otherDVM.getX() + ", " + otherDVM.getY());
            return true;
        }
    }

    public void showSelectItemPage() {
        int errno = 0;

        while(!(errno == 1)) {
            System.out.println("콜라(01)     사이다(02)     녹차(03)      홍차(04)\n" +
                    "밀크티(05)   탄산수(06)     보리차(07)     캔커피(08)\n" +
                    "물(09)      에너지드링크(10) 바닷물(11)    식혜(12)\n" +
                    "아이스티(12) 딸기주스(14)    오렌지주스(15) 포도주스(16)\n" +
                    "이온음료(17) 아메리카노(18)   핫초코(19)    카페라뗴(20)");
            System.out.println("(메뉴 선택으로 돌아가려면 \"0\"을 입력해 주세요.)\n");
            System.out.println("원하시는 음료의 번호를 입력해주세요.");
            System.out.print(">");
            try{
                drinkCode = scan.next();
            }
            catch(InputMismatchException ime) {
                System.out.println("잘못된 입력입니다.");
                continue;
            }

            errno = isValidCode();
            if(errno == 0){
                System.out.println("음료 선택을 취소합니다.");
                return ;
            } else if (errno == -1) {
                System.out.println("0~20 사이의 음료코드를 입력해주세요.");
            }
        }

        errno = 0;
        while(!(errno == 1)) {
            System.out.println("수량을 입력해주세요.");
            System.out.print(">");
            try{
                drinkCount = scan.nextInt();
            }
            catch(InputMismatchException ime) {
                scan.next();
                System.out.println("잘못된 입력입니다. 정수만 입력해주세요.");
                continue;
            }

            errno = isValidCount();
            if(errno == 0){
                System.out.println("음료 선택을 취소합니다.");
                return ;
            } else if (errno == -1) {
                System.out.println("1~999 사이애서 개수를 입력해주세요.");
            }
        }

        scan.nextLine();
        if(myDVM.checkStock(Integer.parseInt(drinkCode), drinkCount)) {
            paymentPage.pay(drinkCode, drinkCount, calculateTotalPrice(), "0");
        } else {
            if(showOtherDVM(getClosestDVM())) {
                System.out.print("안내된 DVM에서 선결제를 진행하시겠습니까?\n" +
                        "(1: 선결제 진행, 나머지 정수: 진행 취소\n" +
                        ">");
                int mode;
                while (true) {
                    try {
                        mode = scan.nextInt();
                        break;
                    } catch (InputMismatchException ime) {
                        scan.next();
                        System.out.println("잘못된 입력입니다. 정수만 입력해주세요.");
                        System.out.print("안내된 DVM에서 선결제를 진행하시겠습니까?\n" +
                                "(1: 선결제 진행, 나머지 정수: 진행 취소\n" +
                                ">");
                    }
                }
                scan.nextLine();
                if(mode == 1) {
                    prePaymentPage.pay(drinkCode, drinkCount, calculateTotalPrice(), msgTeamID);
                }
                else{
                    System.out.println("선결제를 진행하지 않고, 메뉴 선택으로 돌아갑니다.");
                }
            }
            else{
                System.out.println("해당 음료에 대한 재고를 보유한 DVM이 존재하지 않습니다.");
            }
        }
    }


    public void showVerificationCodeMenu() {
        verificationCodeMenu.pay("1", 0, 0, "0");
    }

    public void showAdminPasswordPage() {
        int menu;

        while(true) {

            System.out.println("<관리자 모드>");
            System.out.println("원하는 작업의 번호를 선택해주세요.");
            System.out.println("(메뉴 선택으로 돌아가려면 \"0\"을 입력해주세요.)");

            System.out.println("1. DVM 정보 관리");
            System.out.println("2. 음료 정보 관리");
            System.out.println("3. 음료 세팅");

            System.out.print(">");

            while(!scan.hasNextInt()) {
                scan.next();
                System.out.println("정확한 번호만 입력하세요");
            }
            menu = scan.nextInt();
            if(menu == 1)
                setDVMInfo();
            else if(menu == 2)
                setDrinkInfo();
            else if(menu == 3)
                setDrinkKinds();
            else if(menu == 0) {
                scan.nextLine();
                return; //showMenu로 돌아감
            }
            else {
                System.out.println("번호는 0~3만 입력하세요");
            }
        }
    }

    public boolean checkAdminPassword(String password) {
        boolean check = myDVM.isValidPassword(password);
        if(!check)
            return false;
        else
            return true;
    }

    public void showAdminMenu() {
        String adminPassword;
        boolean check;



        while(true) {
            System.out.println("Admin password를 입력해 주세요");
            System.out.println("(메뉴 선택으로 돌아가려면 \"0\"을 입력해주세요.)");
            System.out.print(">");

            adminPassword = scan.nextLine();
            if(adminPassword.equals("0")) {
                return; //showMenu로 돌아감
            }
            else {
                check = checkAdminPassword(adminPassword);
                if (check) {
                    showAdminPasswordPage();
                }
                else {
                    System.out.println("비밀번호가 틀렸습니다. 다시 입력하세요");
                }
            }
        }
    }

    public void setDVMInfo() {
        String name = "Team4";
        String inputId;
        int x = 0;
        int y = 0;
        Location loc;

        System.out.println("<DVM 정보 관리>");
        System.out.println("id 입력 후 tab을 눌러 좌표를 입력하세요\n");
        System.out.println("id  좌표");
        System.out.println("EX: Team4   10 20");


        while(true) {
            System.out.print(">");
            inputId = scan.next();
            if(inputId.equals(name)) {
                break;
            } else {
                System.out.println("DVM의 id를 확인 후 입력하세요");
            }
        }

        while(true) {
            while(!scan.hasNextInt()) {
                scan.next();
                System.out.println("정확한 번호만 입력하세요");
            }
            x = scan.nextInt();
            if(x >= 0 && x <= 99)
                break;
            else
                System.out.println("x 좌표의 범위는 0~99입니다. 다시 입력하세요");
        }

        while(true) {
            while(!scan.hasNextInt()) {
                scan.next();
                System.out.println("정확한 번호만 입력하세요");
            }
            y=scan.nextInt();
            if(y >= 0 && y <= 99)
                break;
            else
                System.out.println("y 좌표의 범위는 0~99입니다. 다시 입력하세요");
        }

        loc=new Location(x, y);
        myDVM.saveDVMInfo(inputId, loc);
    }

    public void setDrinkInfo() {
        Item[] myItem = myDVM.getItemList();

        System.out.println("<음료 정보 관리>");
        System.out.println("음료 정보 관리 시 tab을 눌러 다음 정보를 입력 후");
        System.out.println("enter를 눌러 다음 음료를 입력하세요.\n");
        System.out.println("음료코드\t\t가격\t\t\t재고\t\t판매여부\t\t음료이름");

        for(int i=0;i<myItem.length;i++) {
            if(myItem[i].getStock()==-1) {
                if(i >= 0 && i <= 8)
                    System.out.println("0" + (i+1) + "\t\t\t" + myItem[i].getPrice() + "\t\t" + "_" + "\t\t" + "X"+ "\t\t\t" + myItem[i].getName() );
                else
                    System.out.println((i+1)  + "\t\t\t" + myItem[i].getPrice() + "\t\t" + "_" + "\t\t" + "X"+ "\t\t\t" + myItem[i].getName());
            }
            else {
                if(i >= 0 && i <= 8)
                    System.out.println("0" + (i+1) +"\t\t\t"+myItem[i].getPrice()+"\t\t"+myItem[i].getStock()+ "\t\t"+"o"+"\t\t\t" + myItem[i].getName() + "\t\t\t");
                else
                    System.out.println((i+1) + "\t\t\t"+myItem[i].getPrice()+"\t\t"+myItem[i].getStock()+"\t\t"+"o"+"\t\t\t" + myItem[i].getName() + "\t\t\t");
                System.out.print(">");

                int price = 0;
                int stock = 0;

                for(int j = 0; j < 2; j++) {
                    while(true) {
                        while (!scan.hasNextInt()) {
                            scan.next();
                            System.out.println("정확한 번호만 입력하세요");
                        }
                        if(j==0) {
                            price = scan.nextInt();
                            if (price > 0)
                                break;
                            else
                                System.out.println("가격은 0보다 커야 합니다. 다시 입력하세요");
                        }
                        else {
                            stock = scan.nextInt();
                            if (stock >= 0)
                                break;
                            else
                                System.out.println("재고는 양수여야 합니다. 다시 입력하세요");
                        }
                    }
                }
                myDVM.saveDrinkInfo(i+1, price, stock, myItem[i].getName());
                scan.nextLine();
            }
        }
    }

    public void setDrinkKinds() {
        int stockCount = 0;
        int drinkCode = 0;
        int[] salesDrinkCodeArr = new int[MAX_SALESNUM];

        System.out.println("<음료 세팅>");
        System.out.println("현재 자판기에서 판매할 7가지 음료의 번호를 입력하고 enter를 눌러주세요");
        System.out.println("콜라(01)     사이다(02)     녹차(03)      홍차(04)\n" +
                "밀크티(05)   탄산수(06)     보리차(07)     캔커피(08)\n" +
                "물(09)      에너지드링크(10) 바닷물(11)    식혜(12)\n" +
                "아이스티(12) 딸기주스(14)    오렌지주스(15) 포도주스(16)\n" +
                "이온음료(17) 아메리카노(18)   핫초코(19)    카페라뗴(20)");

        while(true) {
            System.out.print(">");
            while(!scan.hasNextInt()) {
                scan.next();
                System.out.println("정확한 번호만 입력하세요");
            }
            drinkCode=scan.nextInt();

            if(drinkCode >= 1 && drinkCode <= 20) {

                salesDrinkCodeArr[stockCount] = drinkCode;
                stockCount++;

                for(int i=0;i<stockCount-1;i++)
                {
                    if(salesDrinkCodeArr[i]==drinkCode){
                        stockCount--;
                        System.out.println("같은 음료가 이미 세팅되어 있습니다.");
                        break;
                    }
                }

                if(stockCount == MAX_SALESNUM) {
                    scan.nextLine();
                    break;
                }
            }
            else {
                System.out.println("번호는 01~20만 입력하세요");
            }
        }
        myDVM.saveDrinkKinds(salesDrinkCodeArr);
    }

    public void receiveMsg(Message msg) {  //myDVM.checkStock() 구현 봐야 함
        String msgType = msg.getMsgType();

        switch(msgType){
            case "StockCheckRequest":
                drinkCode = msg.getMsgDescription().getItemCode();
                drinkCount = msg.getMsgDescription().getItemNum();
                if(myDVM.checkStock(Integer.parseInt(drinkCode), drinkCount)) {
                    myMessageManager.sendResMsg("StockCheckResponse", drinkCode, drinkCount, myDVM.getId(), myDVM.getLocation());
                }
                break;
            case "SalesCheckRequest":
                drinkCode = msg.getMsgDescription().getItemCode();
                drinkCount = msg.getMsgDescription().getItemNum();
                if(myDVM.checkStock(Integer.parseInt(drinkCode), drinkCount)){
                    if(myDVM.updateStock(Integer.parseInt(drinkCode), drinkCount)){
                        myMessageManager.sendResMsg("SalesCheckResponse", drinkCode, myDVM.getId(), myDVM.getLocation());
                    }
                }
                break;
            case "PrepaymentCheck":
                drinkCode = msg.getMsgDescription().getItemCode();
                myDVM.saveVerificationCode(msg.getMsgDescription().getAuthCode(), Integer.parseInt(drinkCode), drinkCount);
                break;
            case "StockCheckResponse":
            case "SalesCheckResponse":
                receivedMsgList.add(msg);
                break;
        }
    }

    public int calculateTotalPrice() {
        return drinkCount * myDVM.getItemPrice(Integer.parseInt(drinkCode));
    }
}