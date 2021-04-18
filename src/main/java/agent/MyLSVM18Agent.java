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

public class MyLSVM18Agent extends AbsLSVM18Agent implements IAgent {
	private final static String NAME = "NM"; // TODO: give your agent a name.

	private Map<String, Double> regionalAvg;
	private Set<String> lastAllo;
	private List<String> best4;

	public MyLSVM18Agent(String name) {
		super(name);
		lastAllo = new HashSet<>();
		// TODO: fill this in (if necessary)
		this.regionalAvg = new HashMap<>();
		this.regionalAvg.put("A", 24.0 / 18);
		this.regionalAvg.put("B", 32.0 / 18);
		this.regionalAvg.put("C", 36.0 / 18);
		this.regionalAvg.put("D", 36.0 / 18);
		this.regionalAvg.put("E", 32.0 / 18);
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
	}

	@Override
	protected Map<String, Double> getBids(Map<String, Double> minBids) {
		// TODO: fill this in

		Map<String, Double> bids = new HashMap<>();
		if (this.getCurrentRound() == 0) {
			for (String s : getProximity()) {
				bids.put(s, Math.min(1.261, this.getValuation(s) * 1));
			}
			if (!this.isNationalBidder()) {
				for (String s : minBids.keySet()) {
					if (getValuation(s) != 0
							&& Math.pow((getValuation(s) - 3) / 17, regionalAvg.get(s) / 5 * 4) >= 0.9) {
						this.best4.add(s);
					}
				}

			}
		} else {
			HashSet<String> myGoods = new HashSet<>();
			for (String s : getProximity()) {
				if ((this.getTentativeAllocation().contains(s) && this.lastAllo.contains(s))) {
					myGoods.add(s);
				} else if (this.getCurrentRound() >= 2 && this.best4.contains(s)
						&& this.getTentativeAllocation().contains(s)) {
					myGoods.add(s);
				}
			}

			for (String s : getProximity())
				if (!myGoods.contains(s)) {
					double originalV = this.getValuation(myGoods);
					myGoods.add(s);
					double newV = this.getValuation(myGoods);
					if (originalV + minBids.get(s) + 0.011 < newV) {
						if (originalV + minBids.get(s) + 1.25 > newV)
							bids.put(s, newV - originalV);
						else
							bids.put(s, minBids.get(s) + 0.011);

					}
					myGoods.remove(s);
				} else {
					double newV = this.getValuation(myGoods);
					myGoods.remove(s);
					double originalV = this.getValuation(myGoods);
					if (originalV + minBids.get(s) < newV) {
						bids.put(s, minBids.get(s) + 0.011);
					}
					myGoods.add(s);
				}

		}
		this.lastAllo = new HashSet<>(this.getTentativeAllocation());
		return bids;
	}

	@Override
	protected void onAuctionStart() {
		// TODO: fill this in (if necessary)
		this.lastAllo.clear();
		this.best4 = new ArrayList<>();
	}

	// @Override
	protected void onAuctionEnd(Map<Integer, Set<String>> allocations, Map<Integer, Double> payments,
			List<List<ITradeMessage>> tradeHistory) {
		// TODO: fill this in (if necessary)
		this.lastAllo.clear();

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
