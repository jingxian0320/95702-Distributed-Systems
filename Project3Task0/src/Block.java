/**
 * Author: Jingxian Bao
 * Last Modified: Feb 26, 2020
 */


import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * This class represents a simple Block.
 *
 * Each Block object has an index - the position of the block on the chain.
 * The first block (the so called Genesis block) has an index of 0.
 *
 * Each block has a timestamp - a Java Timestamp object, it holds the time of the block's creation.
 *
 * Each block has a field named data - a String holding the block's single transaction details.
 *
 * Each block has a String field named previousHash - the SHA256 hash of a block's parent.
 * This is also called a hash pointer.
 *
 * Each block holds a nonce - a BigInteger value determined by a proof of work routine.
 * This has to be found by the proof of work logic.
 * It has to be found so that this block has a hash of the proper difficulty.
 * The difficulty is specified by a small integer representing the number of leading hex zeroes the hash must have.
 *
 * Each block has a field named difficulty - it is an int that specifies
 * the exact number of left most hex digits needed by a proper hash.
 * The hash is represented in hexadecimal. If, for example,
 * the difficulty is 3, the hash must have three leading hex 0's
 * (or,1 and 1/2 bytes). Each hex digit represents 4 bits.
 */

public class Block {
    int index;
    Timestamp timestamp;
    String data;
    String previousHash;
    BigInteger nonce;
    int difficulty;

    /**
    This the Block constructor.
     @param index - This is the position within the chain. Genesis is at 0.
     @param timestamp - This is the time this block was added.
     @param data - This is the transaction to be included on the blockchain.
     @param difficulty - This is the number of leftmost nibbles that need to be 0.
     */
    Block(int index, Timestamp timestamp, String data, int difficulty){
        this.index=index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * @return a String holding Hexadecimal characters
     */
    public String calculateHash(){
        String s = "";
        s += String.valueOf(this.index);
        s += this.timestamp.toString();
        s += this.data;
        s += this.previousHash;
        s += this.nonce;
        s += this.difficulty;
        String hash = Hash.ComputeSHA_256_as_Hex_String(s);
        return hash;
    }

    /**
     * This method returns the nonce for this block.
     * The nonce is a number that has been found to cause the
     * hash of this block to have the correct number of leading hexadecimal zeroes.
     * @return a BigInteger representing the nonce for this block.
     */
    public BigInteger getNonce(){
        return this.nonce;
    }

    /**
     * The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
     *
     * This method calls calculateHash() to compute a hash of the concatenation of the index,
     * timestamp, data, previousHash, nonce, and difficulty.
     * If the hash has the appropriate number of leading hex zeroes,
     * it is done and returns that proper hash.
     * If the hash does not have the appropriate number of leading hex zeroes,
     * it increments the nonce by 1 and tries again.
     * It continues this process, burning electricity and CPU cycles,
     * until it gets lucky and finds a good hash.
     * @return a String with a hash that has the appropriate number of leading hex zeroes.
     * The difficulty value is already in the block. This is the number of hex 0's a proper hash must have.
     */

    public String proofOfWork(){
        this.nonce = BigInteger.ZERO;
        while (true){
            String hash = calculateHash();
            if (hash.substring(0,this.difficulty).equals("0".repeat(this.difficulty))){
                return hash;
            }
            else{
                this.nonce = this.nonce.add(BigInteger.ONE);
            }
        }
    }

    /**
     * Simple getter method
     * @return difficulty
     */

    public int getDifficulty(){
        return this.difficulty;
    }

    /**
     * Simple setter method
     * @param difficulty determines how much work is required to produce a proper hash
     */
    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    @Override
    /**
     * Override Java's toString method
     * @return A JSON representation of all of this block's data is returned
     */
    public String toString(){
        String jsonString = "{\"index\" : \"";
        jsonString += String.valueOf(this.index);
        jsonString += "\", \"time stamp\" : \"";
        jsonString += this.timestamp.toString();
        jsonString += "\", \"Tx\" : \"";
        jsonString += this.data;
        jsonString += "\", \"PrevHash\" : \"";
        jsonString += this.previousHash;
        jsonString += "\", \"nonce\" : ";
        jsonString += this.nonce;
        jsonString += ", \"difficulty\" : ";
        jsonString += String.valueOf(this.difficulty);
        jsonString += "}";
        return jsonString;
    }

    /**
     * Simple setter method
     * @param previousHash - a hashpointer to this block's parent
     */
    public void setPreviousHash(String previousHash){
        this.previousHash = previousHash;
    }

    /**
     * Simple getter method
     * @return previous hash
     */
    public String getPreviousHash(){
        return this.previousHash;
    }

    /**
     * Simple getter method
     * @return index of block
     */
    public int getIndex(){
        return this.index;
    }

    /**
     * Simple setter method
     * @param index the index of this block in the chain
     */
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * Simple setter method
     * @param timestamp of when this block was created
     */
    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }

    /**
     * Simple getter method
     * @return timestamp of this block
     */
    public Timestamp getTimestamp(){
        return this.timestamp;
    }

    /**
     * Simple getter method
     * @return this block's transaction
     */
    public String getData(){
        return this.data;
    }

    /**
     * Simple setter method
     * @param data represents the transaction held by this block
     */
    public void setData(String data){
        this.data = data;
    }

    public static void main(java.lang.String[] args){
    }

}
