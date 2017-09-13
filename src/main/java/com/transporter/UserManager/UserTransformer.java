package com.transporter.UserManager;

import com.transporter.Model.User;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.UUID;

public class UserTransformer implements TransformerSupplier<String,User,KeyValue<String,User>> {

    @Override
    public Transformer<String, User, KeyValue<String, User>> get() {
        return new UserTransformerInner();
    }

    public static class UserTransformerInner implements Transformer<String,User,KeyValue<String,User>> {
        ProcessorContext context;
        KeyValueStore<String,User> userStateStore;
        @Override
        public void init(ProcessorContext processorContext) {
            this.context=processorContext;
            this.userStateStore=(KeyValueStore<String, User>) context.getStateStore(" ");
        }

        @Override
        public KeyValue<String, User> transform(String s, User user) {
            if(userStateStore.get(user.getEmail())==null)
            {
                user.setUserId(UUID.randomUUID().toString());

                userStateStore.put(user.getEmail(),user);
                context.forward(user.getUserId(),user);
            }
            return null;
        }

        @Override
        public KeyValue<String, User> punctuate(long l) {
            return null;
        }

        @Override
        public void close() {

        }
    }
}
