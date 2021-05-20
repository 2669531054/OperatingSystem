import java.util.*;


class FreeBlock
{
    int blockSize;                                 //空闲分区的长度
    int blockMinSize = 1;                          //空闲分区最小长度为1
    int number;                                    //空闲分区的编号，初始为1
    int startAddress;                              //空闲分区的起始地址
    String state;                                  //空闲分区的状态，free或occupied
    Job job;                                       //放入的作业

    FreeBlock(int number, int startAddress, int blockSize)
    {
        this.number = number;
        this.startAddress = startAddress;
        this.blockSize = blockSize;
        this.state = "free";
        this.job = new Job();
    }
}





class Job
{
    String jobName = "无作业";     //作业名称
    int startAddress = 0;   //作业起始地址
    int jobSize = 0;        //作业长度

    Job() {}
    Job(String jobName, int jobSize)
    {
        this.jobName = jobName;
        this.jobSize = jobSize;
    }
}


class Solve
{
    LinkedList<FreeBlock> freeBlocks = new LinkedList<>(); //空闲分区链
    LinkedList<Job> jobs = new LinkedList<>();             //作业链
    LinkedList<FreeBlock> blocks = new LinkedList<>();     //所有分区的链


    void sortByAddress(LinkedList<FreeBlock> blocks)            //将分区链中的分区按地址升序排列
    {
        Collections.sort(blocks,
                new Comparator<FreeBlock>()
                {
                    @Override
                    public int compare(FreeBlock block1, FreeBlock block2)
                    {
                        if (block1.startAddress > block2.startAddress)
                        {
                            return 1;
                        }
                        else if(block1.startAddress < block2.startAddress)
                        {
                            return -1;
                        }
                        else
                        {
                            return 0;
                        }
                    }
                });
    }


    void printFreeBlockInfo()                        //将空闲分区链中的分区按地址升序排列
    {
        System.out.println("空闲分区链的信息：");
        System.out.println("空闲分区号  起始地址  结束地址  长度");
        sortByAddress(freeBlocks);
        for (FreeBlock freeBlock : freeBlocks)
        {
            System.out.println("    " + (freeBlocks.indexOf(freeBlock) + 1) + "         " +
                    freeBlock.startAddress + "        " + (freeBlock.startAddress + freeBlock.blockSize)
                    + "       " + freeBlock.blockSize);
        }
        System.out.println();
    }



    void printBlockInfo()                             //按地址升序显示分区情况
    {
        System.out.println("所有分区的信息：");
        System.out.println("起始地址  结尾地址  长度    状态    作业名称  作业大小");
        sortByAddress(blocks);
        for (FreeBlock block : blocks)
        {
            if(block.state.equals("free"))
            {

            }
            System.out.println("   "+block.startAddress + "       " + (block.startAddress + block.blockSize) +
                    "       " + block.blockSize + "     " + block.state + "     " +
                    block.job.jobName + "     " + block.job.jobSize);
        }
        System.out.println();
    }


    int doAllocation(int size, FreeBlock freeBlock)         //分配的过程，返回值为分配的空间大小
    {
        if (freeBlock.blockSize - size <= freeBlock.blockMinSize)         //如果剩余的大小过小，则全部给当前作业
        {
            freeBlocks.remove(freeBlock);
            return freeBlock.blockSize;
        }
        else                                        //否则，分为两个分区，并改变分区链，删去原来的分区，添加剩下的分区
        {
            FreeBlock restBlock = new FreeBlock(freeBlock.number, freeBlock.startAddress + size,
                    freeBlock.blockSize - size);
            freeBlock.blockSize = size;
            freeBlocks.remove(freeBlock);
            blocks.remove(freeBlock);
            freeBlocks.add(restBlock);
            blocks.add(freeBlock);
            blocks.add(restBlock);
            sortByAddress(blocks);
            sortByAddress(freeBlocks);
            return size;
        }
    }


    int judge(int index, int formerIndex, int nextIndex)   //欲释放分区的相邻分区的情况
    {
        if (index != 0 && index != blocks.size() - 1)
        {
            FreeBlock block0 = blocks.get(formerIndex);
            FreeBlock block2 = blocks.get(nextIndex);
            if (block0.state.equals("free") && block2.state.equals("free"))
            {
                return 1;//表示前后都为空闲
            }
            else if (!block0.state.equals("free") && block2.state.equals("free"))
            {
                return 2;//表示前面为占用，后面为空闲
            }
            else if (block0.state.equals("free") && !block2.state.equals("free"))
            {
                return 3;//表示前面为空闲，后面为占用
            }
            else
            {
                return 4;//表示只有当前分区为空闲
            }
        }
        else if (index == 0)
        {
            FreeBlock block2 = blocks.get(nextIndex);
            if (block2.state.equals("free"))
            {
                return 2;
            }
            else
            {
                return 4;
            }
        }
        else
        {
            FreeBlock block0 = blocks.get(formerIndex);
            if (block0.state.equals("free"))
            {
                return 3;
            }
            else
            {
                return 4;
            }
        }
    }


    void doCollection(Job job)                 //回收内存
    {
        sortByAddress(blocks);
        sortByAddress(freeBlocks);
        for (int i = 0; i < blocks.size(); i++)         //寻找作业在哪个分区中
        {
            if (blocks.get(i).job.jobName.equals(job.jobName))
            {
                int index = blocks.indexOf(blocks.get(i));
                int formerIndex = index - 1;
                int nextIndex = index + 1;
                int situation = judge(index, formerIndex, nextIndex);

                switch (situation)
                {
                    case 1:
                    {
                        FreeBlock block0 = blocks.get(formerIndex);
                        FreeBlock block1 = blocks.get(index);
                        FreeBlock block2 = blocks.get(nextIndex);
                        blocks.remove(block0);
                        blocks.remove(block1);
                        blocks.remove(block2);
                        freeBlocks.remove(block0);
                        freeBlocks.remove(block2);
                        FreeBlock block3 = new FreeBlock(0, block0.startAddress, block0.blockSize + block1.blockSize + block2.blockSize);
                        blocks.add(block3);
                        freeBlocks.add(block3);
                        break;
                    }
                    case 2:
                    {
                        FreeBlock block1 = blocks.get(index);
                        FreeBlock block2 = blocks.get(nextIndex);
                        blocks.remove(block1);
                        blocks.remove(block2);
                        freeBlocks.remove(block2);
                        FreeBlock block3 = new FreeBlock((0), block1.startAddress, block1.blockSize + block2.blockSize);
                        blocks.add(block3);
                        freeBlocks.add(block3);
                        break;
                    }
                    case 3:
                    {
                        FreeBlock block0 = blocks.get(formerIndex);
                        FreeBlock block1 = blocks.get(index);
                        blocks.remove(block0);
                        blocks.remove(block1);
                        freeBlocks.remove(block0);
                        FreeBlock block3 = new FreeBlock(0, block0.startAddress, block0.blockSize + block1.blockSize);
                        blocks.add(block3);
                        freeBlocks.add(block3);
                        break;
                    }
                    case 4:
                    {
                        FreeBlock block1 = blocks.get(index);
                        blocks.remove(block1);
                        FreeBlock block3 = new FreeBlock(0, block1.startAddress, block1.blockSize);
                        blocks.add(block3);
                        freeBlocks.add(block3);
                        break;
                    }
                    default:
                    {
                        System.out.println("wrong" + situation);
                        break;
                    }

                }
                sortByAddress(blocks);
                sortByAddress(freeBlocks);
            }
        }
    }


    void solution()                              //首次适应算法
    {
        int counter;         //for循环中的计数器

        System.out.println("输入空闲分区大小");         //初始化空闲分区，并加入空闲分区链表
        Scanner scanner0 = new Scanner(System.in);
        int blockSize = scanner0.nextInt();
        FreeBlock freeBlock = new FreeBlock(1, 0, blockSize);
        freeBlocks.add(freeBlock);
        blocks.add(freeBlock);


        while (true)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("是否有作业进入（y for 有，n for 无）");
            if (scanner.nextLine().equals("y"))                         //有新作业进入
            {
                System.out.println("输入作业名称和长度");
                String jobName = scanner.nextLine();
                int jobSize = scanner.nextInt();
                Job job = new Job(jobName, jobSize);
                jobs.add(job);

                for (counter = 0; counter < freeBlocks.size(); counter++)              //为新加入的作业寻找并分配空闲块
                {
                    FreeBlock tempBlock = freeBlocks.get(counter);
                    if (tempBlock.state.equals("free") && tempBlock.blockSize >= job.jobSize)
                    {
                        tempBlock.job = job;
                        tempBlock.state = "occupied";
                        job.startAddress = tempBlock.startAddress;
                        System.out.println("成功为作业" + job.jobName + "分配大小为"
                                + doAllocation(job.jobSize, tempBlock) + "的内存空间，起始地址为"+job.startAddress);
                        break;
                    }
                }
                if (counter == freeBlocks.size())
                {
                    System.out.println("作业"+job.jobName+"无法加入内存中");
                }
            }


            printBlockInfo();
            printFreeBlockInfo();


            System.out.println("是否有作业已经结束（y for 有，n for 无）");
            Scanner scanner1 = new Scanner(System.in);
            if(scanner1.nextLine().equals("y"))
            {
                System.out.println("输入已经结束的作业的名称");
                String name = scanner1.nextLine();
                for (counter = 0; counter < jobs.size(); counter++)
                {
                    Job tempJob = jobs.get(counter);
                    if (tempJob.jobName.equals(name))
                    {
                        jobs.remove(tempJob);
                        doCollection(tempJob);
                        break;
                    }
                }
                if (counter == jobs.size())
                {
                    System.out.println("该作业不在内存中");
                }
            }
            printBlockInfo();
            printFreeBlockInfo();
        }

    }

    public static void main(String[] args)
    {
        Solve solve = new Solve();
        solve.solution();
    }
}
