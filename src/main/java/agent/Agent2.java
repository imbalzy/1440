package agent;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.ParseException;

import brown.communication.messages.ITradeMessage;
import brown.simulations.OfflineSimulation;
import brown.system.setup.library.Setup;
import brown.user.agent.IAgent;
import brown.user.agent.library.AbsLSVM18Agent;
import brown.user.agent.library.OnlineAgentBackend;

public class Agent2 extends AbsLSVM18Agent implements IAgent {
	private final static String NAME = "NM"; // TODO: give your agent a name.

	private Map<String, Double> regionalAvg;
	private Map<String, Double> lastBids;
	private Map<Double, Double> avgPrice;

	public Agent2(String name) {
		super(name);
		lastBids = new HashMap<>();
		// TODO: fill this in (if necessary)
		this.regionalAvg = new HashMap<>();
		this.regionalAvg.put("A", 30.0 / 18);
		this.regionalAvg.put("B", 40.0 / 18);
		this.regionalAvg.put("C", 45.0 / 18);
		this.regionalAvg.put("D", 45.0 / 18);
		this.regionalAvg.put("E", 40.0 / 18);
		this.regionalAvg.put("F", 30.0 / 18);
		this.regionalAvg.put("G", 35.0 / 18);
		this.regionalAvg.put("H", 50.0 / 18);
		this.regionalAvg.put("I", 55.0 / 18);
		this.regionalAvg.put("J", 55.0 / 18);
		this.regionalAvg.put("K", 50.0 / 18);
		this.regionalAvg.put("L", 35.0 / 18);
		this.regionalAvg.put("M", 30.0 / 18);
		this.regionalAvg.put("N", 40.0 / 18);
		this.regionalAvg.put("O", 45.0 / 18);
		this.regionalAvg.put("P", 45.0 / 18);
		this.regionalAvg.put("Q", 40.0 / 18);
		this.regionalAvg.put("R", 30.0 / 18);
		this.avgPrice = new HashMap<>();
		this.avgPrice.put(1.1, 1.);
	}

	@Override
	protected Map<String, Double> getBids(Map<String, Double> minBids) {
		// TODO: fill this in

		Map<String, Double> bids = new HashMap<>();
		if (this.getCurrentRound() == 0) {
			for (String s : getProximity()) {
				bids.put(s, Math.min(1.261, this.getValuation(s) * 1));
			}
		} else {
			HashSet<String> myGoods = new HashSet<>();
			for (String s : getProximity()) {
				if (this.getTentativeAllocation().contains(s) && !this.lastBids.containsKey(s)) {
					myGoods.add(s);
				}
			}
			if (this.isNationalBidder()) {
				for (String s : getProximity())
					if (!myGoods.contains(s)) {
						double originalV = this.getValuation(myGoods);
						myGoods.add(s);
						double newV = this.getValuation(myGoods);
						if (originalV + minBids.get(s) - 1.25 < newV) {
							bids.put(s, minBids.get(s) + 0.01);
						}
						myGoods.remove(s);
					}
			} else {
				for (String s : getProximity())
					if (!myGoods.contains(s)) {
						double originalV = this.getValuation(myGoods);
						myGoods.add(s);
						double newV = this.getValuation(myGoods);
						if (originalV + minBids.get(s) + 0.011 < newV) {
							if (originalV + minBids.get(s) + 1.25 > newV)
								bids.put(s, newV - originalV);
							else
								bids.put(s, minBids.get(s) + 0.01);

						}
						myGoods.remove(s);
					}
			}
		}
		this.lastBids = bids;
		return bids;
	}

	@Override
	protected void onAuctionStart() {
		// TODO: fill this in (if necessary)
		this.lastBids.clear();

	}

	@Override
	protected void onAuctionEnd(Map<Integer, Set<String>> allocations, Map<Integer, Double> payments,
			List<List<ITradeMessage>> tradeHistory) {
		// TODO: fill this in (if necessary)
		this.lastBids.clear();

	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 0) {
			// Don't change this.
			new OfflineSimulation("offline_test_config.json", "input_configs/lsvm_smra_offline.json", "outfile", false)
					.run();
		} else {
			// Don't change this.
			MyLSVM18Agent agent = new MyLSVM18Agent(NAME);
			agent.addAgentBackend(new OnlineAgentBackend("localhost", Integer.parseInt(args[0]), new Setup(), agent));
			while (true) {
			}
		}
	}

}
