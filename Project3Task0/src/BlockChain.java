/**
 * Author: Jingxian Bao
 * Last Modified: Feb 26, 2020
 */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *This class represents a simple BlockChain.
 */

public class BlockChain {
    /**
     * This BlockChain has exactly two instance members -
     * an ArrayList to hold Blocks and a chain hash to hold a
     * SHA256 hash of the most recently added Block.
     * This constructor creates an empty ArrayList for Block storage.
     * This constructor sets the chain hash to the empty string.
     */
    ArrayList<Block> chain;
    String chainHash;
    public BlockChain(){
        this.chain = new ArrayList<Block>();
        this.chainHash = "";
    }

    /**
     * get the time of the current system
     * @return the current system time
     */
    public Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * get the last block
     * @return a reference to the most recently added Block.
     */
    public Block getLatestBlock(){
        return this.chain.get(this.chain.size()-1);
    }

    /**
     *
     * @return the size of the chain in blocks.
     */
    public int getChainSize(){
        return this.chain.size();
    }

    /**
     *
     * @return hashes per second of the computer holding this chain. It uses a simple string - "00000000" to hash.
     */
    public int hashesPerSecond(){
        Long startTime = System.currentTimeMillis();
        int numHash = 0;
        while (numHash < 1000000){
            Hash.ComputeSHA_256_as_Hex_String("00000000");
            numHash ++;
        }
        Long endTime = System.currentTimeMillis();
        return (int) (numHash / ((endTime - startTime)/1000.0));
    }

    /**
     * A new Block is being added to the BlockChain.
     * This new block's previous hash must hold the hash
     * of the most recently added block. After this call on addBlock,
     * the new block becomes the most recently added block on the BlockChain.
     * The SHA256 hash of every block must exhibit proof of work,
     * i.e., have the requisite number of leftmost 0's defined by its difficulty.
     * Suppose our new block is x. And suppose the old blockchain was
     * a <-- b <-- c <-- d then the chain after addBlock completes is
     * a <-- b <-- c <-- d <-- x. Within the block x, there is a previous hash field.
     * This previous hash field holds the hash of the block d.
     * The block d is called the parent of x.
     * The block x is the child of the block d.
     * It is important to also maintain a hash of the most recently added block in a chain hash.
     * Let's look at our two chains again. a <-- b <-- c <-- d.
     * The chain hash will hold the hash of d. After adding x,
     * we have a <-- b <-- c <-- d <-- x. The chain hash now holds the hash of x.
     * The chain hash is not defined within a block but is defined within the block chain.
     * The arrows are used to describe these hash pointers.
     * If b contains the hash of a then we write a <-- b.
     * @param newBlock is added to the BlockChain as the most recent block
     */
    public void addBlock(Block newBlock){
        newBlock.setPreviousHash(this.chainHash);
        this.chain.add(newBlock);
        this.chainHash = newBlock.proofOfWork();
    }

    public String toString(){
        String s = "{\"ds_chain\" : [";
        for (int i = 0; i < this.chain.size(); i++){
            s += this.chain.get(i);
            if (i != this.chain.size()-1){
                s += ",\n";
            }
            else{
                s += "\n";
            }
        }
        s += "], \"chainHash\":\"";
        s += this.chainHash;
        s += "\"}";
        return s;
    }

    /**
     * If the chain only contains one block,
     * the genesis block at position 0, this routine computes
     * the hash of the block and checks that the hash has
     * the requisite number of leftmost 0's (proof of work)
     * as specified in the difficulty field. It also checks
     * that the chain hash is equal to this computed hash.
     * If either check fails, return false. Otherwise, return true.
     * If the chain has more blocks than one, begin checking from block one.
     * Continue checking until you have validated the entire chain.
     * The first check will involve a computation of a hash in Block 0 and
     * a comparison with the hash pointer in Block 1.
     * If they match and if the proof of work is correct,
     * go and visit the next block in the chain.
     * At the end, check that the chain hash is also correct.
     *
     * @return true if and only if the chain is valid
     */
    public boolean isChainValid(){
        Block thisBlock;
        String thisHash;
        String prevHash = "";
        for (int i = 0; i < this.chain.size(); i++){
            thisBlock = this.chain.get(i);
            if (!thisBlock.previousHash.equals(prevHash)){
                System.out.printf("..Improper previousHash on node %d Does not match with %s\n",i, prevHash);
                return false;
            }
            thisHash = thisBlock.calculateHash();
            if (!thisHash.substring(0,thisBlock.difficulty).equals("0".repeat(thisBlock.difficulty))){
                System.out.printf("..Improper hash on node %d Does not begin with %s\n",i,"0".repeat(thisBlock.difficulty));
                return false;
            }
            prevHash = thisHash;
        }
        if (chainHash.equals(prevHash)){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * This routine repairs the chain.
     * It checks the hashes of each block and ensures
     * that any illegal hashes are recomputed.
     * After this routine is run, the chain will be valid.
     * The routine does not modify any difficulty values.
     * It computes new proof of work based on the difficulty specified in the Block.
     */
    public void repairChain(){
        Block thisBlock;
        String thisHash = "";
        for (int i = 0; i < this.chain.size(); i++){
            thisBlock = this.chain.get(i);
            thisBlock.setPreviousHash(thisHash);
            thisHash = thisBlock.proofOfWork();
        }
        this.chainHash = thisHash;
    }

    public static void main(java.lang.String[] args) {
        // create a block chain
        BlockChain bc = new BlockChain();
        Block genesisBlock = new Block(0, bc.getTime(), "Genesis", 2);
        bc.addBlock(genesisBlock);
        Scanner s = new Scanner(System.in);
        Long startTime;
        Long endTime;
        while(true){
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the Corruption by recomputing hashes.\n" +
                    "6. Exit");
            int opt = s.nextInt();
            switch (opt){
                case 0:
                    System.out.println("Current size of chain: " + bc.getChainSize());
                    System.out.println("Current hashes per second by this machine: " + bc.hashesPerSecond());
                    System.out.println("Difficulty of most recent block: " + bc.getLatestBlock().getDifficulty());
                    System.out.println("Nonce for most recent block: " + bc.getLatestBlock().getNonce());
                    System.out.println("Chain hash: " + bc.chainHash);
                    break;
                case 1:
                    System.out.println("Enter difficulty > 0");
                    int difficulty = s.nextInt();
                    System.out.println("Enter transaction");
                    s.nextLine();
                    String data = s.nextLine();
                    Block newBlock = new Block(bc.getChainSize(), bc.getTime(), data, difficulty);
                    startTime = System.currentTimeMillis();
                    bc.addBlock(newBlock);
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                    break;
                case 2:
                    System.out.println("Verifying entire chain");
                    startTime = System.currentTimeMillis();
                    System.out.println("Chain verification: " + bc.isChainValid());
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 3:
                    System.out.println(bc.toString());
                    break;
                case 4:
                    System.out.println("Corrupt the Blockchain");
                    System.out.print("Enter block ID of block to Corrupt: ");
                    int blockId = s.nextInt();
                    System.out.print("Enter new data for block " + blockId + ":\n");
                    s.nextLine();
                    String corruptData = s.nextLine();
                    Block corruptBlock = bc.chain.get(blockId);
                    corruptBlock.setData(corruptData);
                    System.out.printf("Block %d now holds %s\n",blockId,corruptData);
                    break;
                case 5:
                    System.out.println("Repairing the entire chain");
                    startTime = System.currentTimeMillis();
                    bc.repairChain();
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 6:
                    System.exit(0);
            }
        }
    }
}
