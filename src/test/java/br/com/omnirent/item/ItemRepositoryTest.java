package br.com.omnirent.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.omnirent.integration.IntegrationTest;

@SpringBootTest
public class ItemRepositoryTest extends IntegrationTest {

	@Autowired
	private ItemRepository itemRepository;
}
