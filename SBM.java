package feta.objectmodels;

import feta.network.DirectedNetwork;
import feta.network.Network;
import feta.network.UndirectedNetwork;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import feta.operations.Star;

import java.util.*;

public class SBM extends ObjectModelComponent{

	public int communitiesNumber_=0;
	public int maxNodes_=1;
	public int centre_=0;
	public int[] nodesNum;
	
	//Data structure mapping node indexes to their communities
	public HashMap <Integer, Integer> nodeCommunity_ = new HashMap <Integer, Integer> ();
	
	//Data structures storing the internal information of communities (reading from JSON)
	public HashMap <Integer, Double> communityProportion_ = new HashMap <Integer, Double> ();//mapping community number to the proportion of nodes
	public HashMap <Integer, Double> communityProbability_ = new HashMap <Integer, Double> ();//mapping community number to the probability of connection within the community

    public void calcNormalisation(UndirectedNetwork network, int[] removed) {
    	ArrayList<Integer> nodes=network.getNodeList();
    	normalisationConstant_=nodes.size();
    	for (int i=0;i<nodes.size();i++) {
			if(nodeCommunity_.containsKey(i)==false) {
				addNodeToCommunity(i);
			}
		}
    	nodesNum=new int[communitiesNumber_];
        for (int a=0;a<communitiesNumber_;a++) {
        	nodesNum[a] = 0;
        }
        //for (Map.Entry<Integer, Integer> entry : nodeCommunity_.entrySet()) {
        	//System.out.println("node:"+entry.getKey()+", and its community:"+entry.getValue());
        //}
        for (int i=0;i<nodes.size();i++) {
        	for (int j=0;j<communitiesNumber_;j++) {
        		if(nodeCommunity_.get(i)==j+1)
        			nodesNum[j] += 1;
        	}
		}
        //for (int b=0;b<communitiesNumber_;b++) {
        	//System.out.println("Node number in community "+(b+1)+": "+nodesNum[b]);
        //}
    }

    public void calcNormalisation(DirectedNetwork net, int[] removed) {
    	ArrayList<Integer> nodes=net.getNodeList();
    	normalisationConstant_=nodes.size();
    	for (int i=0;i<nodes.size();i++) {
			if(nodeCommunity_.containsKey(i)==false) {
				addNodeToCommunity(i);
			}
		}
    	nodesNum=new int[communitiesNumber_];
        for (int a=0;a<communitiesNumber_;a++) {
        	nodesNum[a] = 0;
        }
        //for (Map.Entry<Integer, Integer> entry : nodeCommunity_.entrySet()) {
        	//System.out.println("node:"+entry.getKey()+", and its community:"+entry.getValue());
        //}
        for (int i=0;i<nodes.size();i++) {
        	for (int j=0;j<communitiesNumber_;j++) {
        		if(nodeCommunity_.get(i)==j+1)
        			nodesNum[j] += 1;
        	}
		}
        //for (int b=0;b<communitiesNumber_;b++) {
        	//System.out.println("Node number in community "+(b+1)+": "+nodesNum[b]);
        //}
    }

    public double calcProbability(DirectedNetwork net, int node) {
    	double pro=0.0;
    	centre_=Star.getCentreNode();
    	if (normalisationConstant_==0.0) {
            return 0.0;
        }
    	if(nodeCommunity_.containsKey(node)==false) {
			addNodeToCommunity(node);
		}
    	int noCom=nodeCommunity_.get(node); 
    	int cenCom=nodeCommunity_.get(centre_);
    	for (int i=0;i<communitiesNumber_;i++) {
    		if (cenCom==i+1) {
    			if (noCom==cenCom) {
    				pro=communityProbability_.get(cenCom)/nodesNum[i];
    			}
    			else {
    				double temp1=1.0-communityProbability_.get(cenCom);
        			double temp2=normalisationConstant_-nodesNum[i];
        			pro=temp1/temp2;
    			}
    		}
    		else continue;
    	}
    	return pro;
    }

    public double calcProbability(UndirectedNetwork net, int node) {
    	double pro=0.0;
    	centre_=Star.getCentreNode();
    	if (normalisationConstant_==0.0) {
            return 0.0;
        }
    	if(nodeCommunity_.containsKey(node)==false) {
			addNodeToCommunity(node);
		}
    	int noCom=nodeCommunity_.get(node); 
    	int cenCom=nodeCommunity_.get(centre_);
    	for (int i=0;i<communitiesNumber_;i++) {
    		if (cenCom==i+1) {
    			if (noCom==cenCom) {
    				pro=communityProbability_.get(cenCom)/nodesNum[i];
    			}
    			else {
    				double temp1=1.0-communityProbability_.get(cenCom);
        			double temp2=normalisationConstant_-nodesNum[i];
        			pro=temp1/temp2;
    			}
    		}
    		else continue;
    	}
    	return pro;
    }
	

    public void parseJSON(JSONObject params) {
        Long maxNodes = (Long) params.get("MaxNodes");
        if (maxNodes!=null) {
        	maxNodes_=maxNodes.intValue();
        	System.out.println("Max nodes:"+maxNodes_);
        }
    	Long communitiesNumber = (Long) params.get("NumberOfCommunities");
        if (communitiesNumber!=null) {
        	communitiesNumber_=communitiesNumber.intValue();
            System.out.println("The number of communities:"+communitiesNumber_);
        }
        JSONArray communities = (JSONArray) params.get("Communities");
        for (int i=0;i<communities.size();i++) {
        	JSONObject key = (JSONObject) communities.get(i);
        	double nodeProportion = (double) key.get("ProportionOfNodes");
        	double withinProbability = (double) key.get("ProbabilityOfConnectionWithinCommunity");
        	communityProportion_.put(i+1, nodeProportion);
            communityProbability_.put(i+1, withinProbability);
        }
        for (Map.Entry<Integer, Double> entry : communityProportion_.entrySet()) {
    		System.out.println("community number:"+entry.getKey()+", and proportion of nodes:"+entry.getValue());
    	}
        for (Map.Entry<Integer, Double> entry : communityProbability_.entrySet()) {
    		System.out.println("community number:"+entry.getKey()+", and probability of connection within community:"+entry.getValue());
    	}
    }
	
    //If the node doesn't have a community, this function will create one for it randomly
    public void addNodeToCommunity(int node) {
    	//compute the rate
    	double sum = 0.0;
    	for (Map.Entry<Integer, Double> entry : communityProportion_.entrySet()) {
    		sum += entry.getValue();
    	}
    	double[] rateArray = new double[communitiesNumber_];
    	for (int i=0;i<communitiesNumber_;i++) {
    		rateArray[i] = communityProportion_.get(i+1)/sum;
    	}
		//create a community randomly according to the rates
		double randomNumber;
		randomNumber = Math.random();
		if (randomNumber >= 0.0 && randomNumber <= rateArray[0]) {
			nodeCommunity_.put(node,1);
		}
		else if (randomNumber >= rateArray[0] && randomNumber <= rateArray[0]+rateArray[1]) {
			nodeCommunity_.put(node,2);
		}
		else if (randomNumber >= rateArray[0]+rateArray[1] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]) {
			nodeCommunity_.put(node,3);
		}
		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]) {
			nodeCommunity_.put(node,4);
		}
		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]) {
			nodeCommunity_.put(node,5);
		}
		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]) {
			nodeCommunity_.put(node,6);
		}
//		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6]) {
//			nodeCommunity_.put(node,7);
//		}
//		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6]+rateArray[7]) {
//			nodeCommunity_.put(node,8);
//		}
//		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6]+rateArray[7] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6]+rateArray[7]+rateArray[8]) {
//			nodeCommunity_.put(node,9);
//		}
//		else if (randomNumber >= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6]+rateArray[7]+rateArray[8] && randomNumber <= rateArray[0]+rateArray[1]+rateArray[2]+rateArray[3]+rateArray[4]+rateArray[5]+rateArray[6]+rateArray[7]+rateArray[8]+rateArray[9]) {
//			nodeCommunity_.put(node,10);
//		}
    }
}
