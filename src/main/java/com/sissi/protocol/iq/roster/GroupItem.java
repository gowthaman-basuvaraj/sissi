package com.sissi.protocol.iq.roster;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.sissi.context.JID;
import com.sissi.io.read.Collector;
import com.sissi.io.read.Metadata;
import com.sissi.protocol.Item;
import com.sissi.protocol.presence.PresenceType;
import com.sissi.ucenter.relation.roster.RosterRelation;

/**
 * @author kim 2013-10-31
 */
@Metadata(uri = Roster.XMLNS, localName = Item.NAME)
@XmlType(namespace = Roster.XMLNS)
@XmlRootElement(name = Item.NAME)
public class GroupItem extends Item implements Collector {

	private Set<Group> groups;

	private String ask;

	private String subscription;

	private RosterNickname nickname;

	public GroupItem() {
		super();
	}

	public GroupItem(JID jid) {
		this(jid.asStringWithBare(), null);
	}

	public GroupItem(RosterRelation roster) {
		this(roster.jid(), roster.name());
		this.setAsk(roster.ask());
		this.setSubscription(roster.subscription());
		if (roster.groups() != null) {
			for (String group : roster.groups()) {
				this.add(new Group(group));
			}
		}
	}

	private GroupItem(String jid, String name) {
		super(jid, name);
	}

	public GroupItem add(Group group) {
		if (this.groups == null) {
			this.groups = new HashSet<Group>();
		}
		this.groups.add(group.item(this));
		return this;
	}

	public GroupItem addOnEmpty(Group group) {
		if (this.getGroup() == null) {
			this.add(group);
		}
		return this;
	}

	public GroupItem nickname(String nick, String def) {
		this.nickname = nick != null ? new RosterNickname(nick) : new RosterNickname(def);
		return this;
	}

	@XmlElement
	public RosterNickname getNickname() {
		return this.nickname;
	}

	@XmlElements({ @XmlElement(name = Group.NAME, type = Group.class) })
	public Set<Group> getGroup() {
		return this.groups != null && !this.groups.isEmpty() ? this.groups : null;
	}

	public void set(String localname, Object ob) {
		this.add(Group.class.cast(ob));
	}

	public GroupItem setAsk(boolean ask) {
		this.ask = ask ? PresenceType.SUBSCRIBE.toString() : null;
		return this;
	}

	@XmlAttribute
	public String getAsk() {
		return RosterSubscription.parse(this.getSubscription()).in(RosterSubscription.NONE, RosterSubscription.FROM) ? this.ask : null;
	}

	@XmlAttribute
	public String getSubscription() {
		return this.subscription;
	}

	public GroupItem setSubscription(String subscription) {
		this.subscription = subscription;
		return this;
	}

	public GroupItem setSubscription(RosterSubscription subscription) {
		this.subscription = subscription.toString();
		return this;
	}

	public boolean action(GroupAction action) {
		return GroupAction.parse(this.getSubscription()) == action;
	}

	public GroupItem trimName(Integer length) {
		if (super.getName() != null && super.getName().length() > length) {
			super.setName(super.getName().substring(0, length));
		}
		return this;
	}

	public GroupItem trimGroup(Integer length) {
		if (this.groups != null) {
			for (Group group : this.groups) {
				group.trim(length);
			}
		}
		return this;
	}
}
