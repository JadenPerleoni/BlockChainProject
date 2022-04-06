import java.util.ArrayList;


/**
 * This holds the values for a Block in the Blockchain, and it has methods to compute the Merkle Root and generate a hash.
 */
public class Block {


    private String sMerkleRoot;
    private int iDifficulty = 6; // Mining seconds in testing 5: 6,10,15,17,20,32 | testing 6: 12,289,218
    private String sNonce;
    private String sMinerUsername;
    private String sHash;



    /**
     * This computes the Merkle Root. It either accepts 2 or 4 items, or if made to be dynamic, then accepts any
     * multiple of 2 (2,4,8.16.32,etc.) items.
     * @param lstItems
     * @return
     */
    public synchronized String computeMerkleRoot(ArrayList<String> lstItems) {
        // Makes an arraylist of Merkle nodes to store all the needed nodes.
        ArrayList<MerkleNode> lstNodes = new ArrayList<>();

        // Checks if the list of transactions is a power of two.
        if (lstItems.size() >= 2 && BlockchainBasics.isPowerOfTwo(lstItems.size())){

            // To find the number of nodes for any given merkle tree, I came up with the formula
            // of (2*n)-1 where n is the number if items. For example if you pass in 4 items,
            // the number of total nodes will be (2*4)-1 = 7.
            for (int i = 0; i < (lstItems.size()*2)-1; i++){

                // Makes a new node every loop.
                lstNodes.add(new MerkleNode());

                // Hashes only the transactions by comparing it to the number of items inputted.
                if (lstNodes.size() <= lstItems.size()) {
                    lstNodes.get(i).sHash = BlockchainUtil.generateHash(lstItems.get(i));
                }
            }

            // We declare this number outside the for loop so that it won't be reset to 0 every loop.
            int count = 0;

            // Integer i starts at the end of list items, because these nodes are empty,
            // and will hold the combined Merkle roots of the transactions.
            for (int i = lstItems.size(); i < lstNodes.size(); i++){

                    // We are getting the first node after the transaction nodes, so we hash the left node,
                    // and we add one to that number to hash the node right next to it.
                    // Eventually, it combines all the nodes until the final node is hashed as the merkle root.
                    populateMerkleNode(lstNodes.get(i), lstNodes.get(count), lstNodes.get(count+1));
                count+= 2;
            }

            // Returns the last item in the
            return lstNodes.get(lstNodes.size()-1).sHash;
        }

        return "Failed to compute merkle root.";
    }



    /**
     * This method populates a Merkle node's left, right, and hash variables.
     * @param oNode
     * @param oLeftNode
     * @param oRightNode
     */
    private void populateMerkleNode(MerkleNode oNode, MerkleNode oLeftNode, MerkleNode oRightNode){

        oNode.oLeft = oLeftNode;
        oNode.oRight = oRightNode;
        oNode.sHash = BlockchainUtil.generateHash(oNode.oLeft.sHash + oNode.oRight.sHash);

    }


    // Hash this block, and hash will also be next block's previous hash.

    /**
     * This generates the hash for this block by combining the properties and hashing them.
     * @return
     */
    public String computeHash() {

        return new BlockchainUtil().generateHash(sMerkleRoot + iDifficulty + sMinerUsername + sNonce);
    }



    public int getDifficulty() {
        return iDifficulty;
    }


    public String getNonce() {
        return sNonce;
    }
    public void setNonce(String nonce) {
        this.sNonce = nonce;
    }

    public void setMinerUsername(String sMinerUsername) {
        this.sMinerUsername = sMinerUsername;
    }

    public String getHash() { return sHash; }
    public void setHash(String h) {
        this.sHash = h;
    }

    public synchronized void setMerkleRoot(String merkleRoot) { this.sMerkleRoot = merkleRoot; }




    /**
     * Run this to test your merkle tree logic.
     * @param args
     */
    public static void main(String[] args){

        ArrayList<String> lstItems = new ArrayList<>();
        Block oBlock = new Block();
        String sMerkleRoot;

        // These merkle root hashes based on "t1","t2" for two items, and then "t3","t4" added for four items.
        String sExpectedMerkleRoot_2Items = "3269f5f93615478d3d2b4a32023126ff1bf47ebc54c2c96651d2ac72e1c5e235";
        String sExpectedMerkleRoot_8Items = "36941ee71cbbb9196c4691026e68c8be5605332842a766bf069d631d2c7c8d47";

        lstItems.add("t1");
        lstItems.add("t2");


        // *** BEGIN TEST 2 ITEMS ***

        sMerkleRoot = oBlock.computeMerkleRoot(lstItems);

        if(sMerkleRoot.equals(sExpectedMerkleRoot_2Items)){

            System.out.println("Merkle root method for 2 items worked!");
        }

        else{
            System.out.println("Merkle root method for 2 items failed!");
            System.out.println("Expected: " + sExpectedMerkleRoot_2Items);
            System.out.println("Received: " + sMerkleRoot);

        }


        // *** BEGIN TEST 8 ITEMS ***

        lstItems.add("t3");
        lstItems.add("t4");
        lstItems.add("t5");
        lstItems.add("t6");
        lstItems.add("t7");
        lstItems.add("t8");


        sMerkleRoot = oBlock.computeMerkleRoot(lstItems);

        if(sMerkleRoot.equals(sExpectedMerkleRoot_8Items)){

            System.out.println("Merkle root method for 8 items worked!");
        }

        else{
            System.out.println("Merkle root method for 8 items failed!");
            System.out.println("Expected: " + sExpectedMerkleRoot_8Items);
            System.out.println("Received: " + sMerkleRoot);

        }
    }
}
