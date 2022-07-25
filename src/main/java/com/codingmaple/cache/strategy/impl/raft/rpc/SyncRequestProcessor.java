package com.codingmaple.cache.strategy.impl.raft.rpc;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import com.codingmaple.cache.strategy.impl.raft.CacheRaftSyncService;
import com.codingmaple.cache.strategy.impl.raft.CacheSyncClosure;
import com.codingmaple.cache.strategy.impl.raft.CacheSyncServer;
import com.codingmaple.cache.strategy.impl.raft.SyncOperation;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class SyncRequestProcessor implements RpcProcessor<SyncRequest> {
		private static final Logger log = LoggerFactory.getLogger(SyncRequestProcessor.class);

		private final CacheSyncServer syncServer;
		private final CacheRaftSyncService syncService;

		public SyncRequestProcessor(CacheSyncServer cacheSyncServer, CacheRaftSyncService syncService) {
				super();
				this.syncServer = cacheSyncServer;
				this.syncService = syncService;
		}

		@Override
		public void handleRequest(RpcContext rpcCtx, SyncRequest request) {
				final CacheSyncClosure closure = new CacheSyncClosure() {
						@Override
						public void run(Status status) {
								rpcCtx.sendResponse(getResultResponse());
						}
				};
//				this.syncService.syncCache(request.getSource(), request.getCacheName(), request.getCacheKey(), closure);
				String cacheName = request.getCacheName();
				String cacheKey = request.getCacheKey();
				applyOperation(SyncOperation.createSync(cacheName, cacheKey), closure);

//				try {
//						this.syncService.syncCache(cacheName, cacheKey);
//				}finally {
//				}

		}

		@Override
		public String interest() {
				return SyncRequest.class.getName();
		}


		private boolean isLeader() {
				return this.syncServer.getFsm().isLeader();
		}

		private String getRedirect(){
				return this.syncServer.redirect().getRedirect();
		}

		private void applyOperation(final SyncOperation op, final CacheSyncClosure closure) {
				if (!isLeader()){
						handleNotLeaderError(closure);
						return;
				}
				try {
						closure.setSyncOperation( op );
						final Task task = new Task();
						task.setData(ByteBuffer.wrap(SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(op)));
						task.setDone(closure);
						this.syncServer.getNode().apply(task);
				}catch (CodecException e) {
						String error = "Fail encode SyncOperation";
						log.error(e.getLocalizedMessage(), e);
						closure.failure(error, StringUtil.EMPTY_STRING);
						closure.run(new Status(RaftError.EINTERNAL, error));
				}
		}

		private void handleNotLeaderError(final CacheSyncClosure closure) {
				closure.failure("Not leader", getRedirect());
				closure.run( new Status(RaftError.EPERM, "Not leader"));
		}
}
