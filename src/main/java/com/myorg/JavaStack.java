package com.myorg;

import io.github.cdklabs.dynamotableviewer.TableViewer;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class JavaStack extends Stack {

    public JavaStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public JavaStack(final Construct parent, final String id, final StackProps props)
    {
        super(parent, id, props);

        final Function hello = Function.Builder.create(this, "HelloHandler")
                .runtime(Runtime.NODEJS_14_X)
                .code(Code.fromAsset("lambda"))
                .handler("hello.handler")
                .build();

        // Defines our hitcounter resource
        final HitCounter helloWithCounter = new HitCounter(this, "HelloHitCounter", HitCounterProps.builder()
                .downstream(hello)
                .build());

        // Defines an API Gateway REST API resource backed by our "hello" function
        LambdaRestApi.Builder.create(this, "Endpoint")
                .handler(helloWithCounter.getHandler())
                .build();

        // Defines a viewer for the HitCounts table
        TableViewer.Builder.create(this, "ViewerHitCount")
                .title("Hello Hits")
                .table(helloWithCounter.getTable())
                .build();
    }
}
