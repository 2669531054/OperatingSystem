import java.util.*;

class PCB
{
    String processName;     //进程名
    int requiredTime1;      //需要CPU时间，会变化
    int requiredTime2;      //总共需要的CPU时间，用于最后计算带权周转时间
    int occupiedTime;       //已获得CPU时间,初始为0
    int arriveTime;         //进入就绪队列时刻
    int startTime;          //第一次获得cpu的时刻
    int endTime;            //完成时刻
    int counter;            //记录进程已获得cpu的次数
    String state;           //进程状态，分为ready、running、end

    PCB(){}
    PCB(String processName,int requiredTime,int arriveTime)
    {
        this.processName = processName;
        this.requiredTime1 = requiredTime;
        this.requiredTime2 = requiredTime;          //作为requiredTime1的副本，用于最后计算带权周转时间
        this.arriveTime = arriveTime;
        this.state = "ready";
    }
}


class RoundRobin
{
    int timePiece;           //时间片长度
    int cpuTime = 0;         //记录cpu已经运行的时间
    Queue<PCB> pcbQueue = new LinkedList<>();    //就绪队列
    Queue<PCB> endQueue = new LinkedList<>();    //结束队列

    RoundRobin(){}

    void printReadyQueueInfo()    //输出就绪队列在一个时间片执行完毕后的信息
    {
        System.out.println();
        System.out.println("当前就绪队列信息(队首到队尾)");
        System.out.println("进程名称  进入时刻  还需要时间  占用时间  状态  cpu已运行时间");
        for (PCB pcb : pcbQueue)
        {
            System.out.println("   "+pcb.processName + "        " + pcb.arriveTime +
                    "        " + pcb.requiredTime1 + "         " +
                    pcb.occupiedTime + "     " + pcb.state + "       " + cpuTime);
        }
    }

    void printEndQueueInfo()     //输出结束队列的信息
    {
        int sum1 = 0;          //记录周转时间总和
        double sum2 = 0;       //记录带权周转时间总和
        System.out.println();
        System.out.println("当前结束队列信息(队首到队尾)");
        System.out.println("进程名称  进入时刻  开始时刻  需要时间  结束时刻  周转时间  带权周转时间");
        for (PCB pcb : endQueue)
        {
            System.out.println("   " + pcb.processName + "        " + pcb.arriveTime + "        "
                    + pcb.startTime + "         " + pcb.requiredTime2 + "         " + pcb.endTime
                    + "         " + (pcb.endTime - pcb.arriveTime) + "           "
                    + (double) (pcb.endTime - pcb.arriveTime) / pcb.requiredTime2);
            sum1 = sum1 + (pcb.endTime - pcb.arriveTime);
            sum2 = sum2 + (double) (pcb.endTime - pcb.arriveTime) / pcb.requiredTime2;
        }
        System.out.println();
        System.out.println("平均周转时间：" + (double) sum1 / endQueue.size());
        System.out.println("平均带权周转时间" + sum2 / endQueue.size());
    }

    void resolve()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入时间片长度");
        timePiece =scanner.nextInt();

        //初始化PCB
        System.out.println("输入0时刻进入的进程的名称、需要占用cpu的时间");
        String processName = scanner.next();
        int requiredTime = scanner.nextInt();
        PCB pcb = new PCB(processName, requiredTime, 0);
        pcbQueue.offer(pcb);


        while(!pcbQueue.isEmpty())     //就绪队列不为空时
        {
            //取队首元素占据cpu一个时间片的时间
            pcbQueue.element().state = "running";
            pcbQueue.element().counter++;
            if (pcbQueue.element().counter == 1)    //若为第一次占用cpu，将当前时刻设置为第一次占据cpu的时刻
            {
                pcbQueue.element().startTime = cpuTime;
            }
            for(int i = 0; i< timePiece; i++)
            {
                cpuTime++;
                pcbQueue.element().occupiedTime += 1;
                pcbQueue.element().requiredTime1 -= 1;

                //每过单位1的时间，判断是否有新进程进入就绪队列，并判断当前队首进程是否结束
                System.out.println("时刻：" + cpuTime + "    此时是否有新进程进入？y for 有，n for 无");
                if (scanner.next().equals("y"))     //有新进程进入就绪队列
                {
                    System.out.println("输入进程名称、需要占用cpu的时间");
                    processName = scanner.next();
                    requiredTime = scanner.nextInt();
                    pcbQueue.offer(new PCB(processName, requiredTime, cpuTime));
                }

                if(pcbQueue.element().requiredTime1 <=0)     //若单位时间'1'后队首进程已执行完毕
                {
                    pcbQueue.element().state = "end";
                    pcbQueue.element().requiredTime1 = 0;
                    pcbQueue.element().endTime = cpuTime;
                    endQueue.offer(pcbQueue.element());    //复制到结束队列
                    pcbQueue.poll();     //移出队列

                    if (i < timePiece - 1)         //提前结束的进程释放时间片，此时的队首进程占用cpu
                    {
                        break;
                    }
                }

            }

            //一个时间片结束后，若就绪队列不为空且队首进程尚未执行完毕（为空报错，注意顺序）
            if (!pcbQueue.isEmpty() && pcbQueue.element().state.equals("running"))
            {
                pcbQueue.element().state = "ready";
                pcbQueue.offer(pcbQueue.element());
                pcbQueue.poll();                //移动到队列尾端
                pcbQueue.element().state = "running";
            }

            printReadyQueueInfo();        //输出一个进程调度结束(即队首进程结束或时间片用完)后就绪队列的信息
            printEndQueueInfo();          //输出一个进程调度结束(即队首进程结束或时间片用完)后结束队列的信息
        }

        System.out.println("总体调度情况：");                       //输出最终信息
        printEndQueueInfo();
    }



    public static void main(String[] args)
    {
        RoundRobin roundRobin = new RoundRobin();
        roundRobin.resolve();
    }
}
