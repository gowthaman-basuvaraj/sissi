package com.sissi.pipeline.in.iq.register.muc;

import com.sissi.context.JIDContext;
import com.sissi.pipeline.in.ProxyProcessor;
import com.sissi.protocol.Error;
import com.sissi.protocol.Protocol;
import com.sissi.protocol.ProtocolType;
import com.sissi.protocol.error.ServerError;
import com.sissi.protocol.error.detail.ItemNotFound;
import com.sissi.ucenter.vcard.VCardContext;

/**
 * 房间真实性校验
 * 
 * @author kim 2014年3月10日
 */
public class RegisterMucCheckExistsProcessor extends ProxyProcessor {

	private final Error error = new ServerError().type(ProtocolType.CANCEL).add(ItemNotFound.DETAIL);

	private final VCardContext vcardContext;

	public RegisterMucCheckExistsProcessor(VCardContext vcardContext) {
		super();
		this.vcardContext = vcardContext;
	}

	@Override
	public boolean input(JIDContext context, Protocol protocol) {
		return this.vcardContext.exists(super.build(protocol.parent().getTo())) ? true : this.writeAndReturn(context, protocol);
	}

	private boolean writeAndReturn(JIDContext context, Protocol protocol) {
		context.write(protocol.parent().reply().setError(this.error));
		return false;
	}
}
