package tianma.ss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import tianma.ss.spider.model.ShadowSocksConfig;

public class Demo {

	@Test
	public void test() throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		ShadowSocksConfig config = gson.fromJson(new FileReader(new File("gui-config.json")), ShadowSocksConfig.class);
		System.out.println(config);

		gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(config);
		FileUtils.write(new File("gui-config2.json"), json);

	}

	@Test
	public void test2() throws JsonIOException, JsonSyntaxException, FileNotFoundException {

		JsonParser parser = new JsonParser();
		JsonObject object = (JsonObject) parser.parse(new FileReader(new File("gui-config.json")));
		System.out.println(object.toString());
	}

}
